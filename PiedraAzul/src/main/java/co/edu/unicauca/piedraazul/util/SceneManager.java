package co.edu.unicauca.piedraazul.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SceneManager {

    private Stage primaryStage;
    private final ApplicationContext context;

    // Spring inyecta el contexto automáticamente
    public SceneManager(ApplicationContext context) {
        this.context = context;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/" + fxmlFile)
            );
            // ← SIN esto, @Autowired en LoginController no funciona
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}