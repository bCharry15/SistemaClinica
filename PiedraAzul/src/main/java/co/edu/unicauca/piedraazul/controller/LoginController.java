package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.service.UserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService;
    private final SceneManager sceneManager;

    public LoginController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userService.authenticate(username, password);

        if (user != null) {
            switch (user.getRole()) {
                case ADMIN -> sceneManager.switchScene("adminPanel.xml");
                case USER  -> sceneManager.switchScene("userPanel.xml");
            }
        } else {
            showAlert("Error", "Usuario o contraseña incorrectos.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}