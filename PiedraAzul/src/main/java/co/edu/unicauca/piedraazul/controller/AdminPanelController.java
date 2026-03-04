package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.UserRole;
import co.edu.unicauca.piedraazul.model.UserStatus;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.service.UserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AdminPanelController implements Observer {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private TextArea logArea;

    private final SceneManager sceneManager;
    private final UserService userService;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss");

    public AdminPanelController(SceneManager sceneManager, UserService userService) {
        this.sceneManager = sceneManager;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        roleChoice.getItems().addAll("ADMIN", "USER");
        // Suscribirse al UserService para eventos generales
        userService.attach(this);
        System.out.println("Panel de administrador cargado");
    }

    // Recibe notificaciones tanto del modelo User como del UserService
    @Override
    public void update(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = "[" + timestamp + "] " + message + "\n";

        Platform.runLater(() -> logArea.appendText(logEntry));
        System.out.println("[Observer] " + logEntry.trim());
    }

    @FXML
    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleChoice.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Todos los campos son obligatorios.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(UserRole.valueOf(role));
        user.setStatus(UserStatus.ACTIVE);

        // Se pasa 'this' como Observer para que el modelo User notifique directamente
        boolean success = userService.registerUser(user, this);

        if (!success) {
            showAlert("Error", "El usuario ya existe.");
        } else {
            usernameField.clear();
            passwordField.clear();
            roleChoice.setValue(null);
        }
    }

    @FXML
    private void logout() {
        userService.detach(this);
        sceneManager.switchScene("login.xml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}