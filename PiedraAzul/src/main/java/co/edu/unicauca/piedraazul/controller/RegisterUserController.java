package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.UserRole;
import co.edu.unicauca.piedraazul.model.UserStatus;
import co.edu.unicauca.piedraazul.service.UserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoice;

    private final UserService userService;
    private final SceneManager sceneManager;

    public RegisterUserController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        roleChoice.getItems().addAll("ADMIN", "USER");
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

        boolean success = userService.registerUser(user);
        if (success) {
            showAlert("Éxito", "Usuario registrado correctamente.");
            clearFields();
        } else {
            showAlert("Error", "El usuario ya existe.");
        }
    }

    @FXML
    private void goBack() {
        sceneManager.switchScene("adminPanel.xml");
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleChoice.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}