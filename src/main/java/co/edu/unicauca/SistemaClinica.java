package co.edu.unicauca;

import co.edu.unicauca.database.DatabaseConnection;
import co.edu.unicauca.database.DatabaseInitializer;
import co.edu.unicauca.repository.PacienteRepositoryImpl;
import co.edu.unicauca.repository.UsuarioRepositoryImpl;
import co.edu.unicauca.service.AuthServiceImpl;
import co.edu.unicauca.service.PacienteServiceImpl;
import co.edu.unicauca.service.UsuarioServiceImpl;
import co.edu.unicauca.ui.LoginView;
import java.sql.Connection;
import javafx.application.Application;
import javafx.stage.Stage;

public class SistemaClinica extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Crear servicios para inyección
        UsuarioRepositoryImpl usuarioRepo = new UsuarioRepositoryImpl();
        PacienteRepositoryImpl pacienteRepo = new PacienteRepositoryImpl();
        PacienteServiceImpl pacienteService = new PacienteServiceImpl(pacienteRepo);
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(usuarioRepo);
        
        // 2. Inicializar base de datos con servicio inyectado
        Connection conn = DatabaseConnection.getInstance();
        new DatabaseInitializer(conn, pacienteService).inicializar();

        // 3. Crear servicios de autenticación
        AuthServiceImpl authService = new AuthServiceImpl(usuarioRepo);

        // 4. Mostrar pantalla de login con servicios inyectados
        new LoginView(authService, pacienteService, usuarioService, stage).mostrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}