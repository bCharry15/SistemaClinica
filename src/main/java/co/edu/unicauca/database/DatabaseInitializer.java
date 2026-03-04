/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.unicauca.database;

import co.edu.unicauca.model.Paciente;
import co.edu.unicauca.model.Usuario;
import co.edu.unicauca.service.IPacienteService;
import co.edu.unicauca.util.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final Connection connection;
    private final IPacienteService pacienteService;

    public DatabaseInitializer(Connection connection, IPacienteService pacienteService) {
        this.connection = connection;
        this.pacienteService = pacienteService;
    }

    public void inicializar() throws SQLException {
        crearTablaUsuarios();
        migrarAgregarEstadoAUsuarios();
        crearTablaPacientes();
        insertarAdminPorDefecto();
        insertarUsuarioNormalPorDefecto();
        insertarPacientesEjemplo();
    }

    private void crearTablaUsuarios() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id            INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username      TEXT    NOT NULL UNIQUE,"
                + "password_hash TEXT    NOT NULL,"
                + "rol           TEXT    NOT NULL CHECK(rol IN ('ADMIN','USER')),"
                + "estado        TEXT    NOT NULL DEFAULT 'ACTIVO' CHECK(estado IN ('ACTIVO','DESACTIVADO'))"
                + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void migrarAgregarEstadoAUsuarios() throws SQLException {
        // Verificar si la columna 'estado' ya existe
        String checkSql = "PRAGMA table_info(usuarios)";
        boolean tieneEstado = false;
        try (Statement stmt = connection.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("estado".equals(columnName)) {
                    tieneEstado = true;
                    break;
                }
            }
        }
        
        // Si no existe, agregarla
        if (!tieneEstado) {
            String addColumnSql = "ALTER TABLE usuarios ADD COLUMN estado TEXT NOT NULL DEFAULT 'ACTIVO' CHECK(estado IN ('ACTIVO','DESACTIVADO'))";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(addColumnSql);
            }
        }
    }

    private void crearTablaPacientes() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS pacientes ("
                + "id               INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre           TEXT    NOT NULL,"
                + "apellido         TEXT    NOT NULL,"
                + "cedula           TEXT    NOT NULL UNIQUE,"
                + "telefono         TEXT,"
                + "correo           TEXT,"
                + "fecha_nacimiento TEXT,"
                + "diagnostico      TEXT"
                + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private void insertarAdminPorDefecto() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM usuarios WHERE username = 'admin'";
        try (Statement stmt = connection.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String hashAdmin = PasswordUtil.hashear("admin123");
                String insertSql = "INSERT INTO usuarios (username, password_hash, rol) "
                        + "VALUES ('admin', '" + hashAdmin + "', 'ADMIN')";
                try (Statement s2 = connection.createStatement()) {
                    s2.execute(insertSql);
                }
            }
        }
    }

    private void insertarUsuarioNormalPorDefecto() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM usuarios WHERE username = 'enfermera1'";
        try (Statement stmt = connection.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String hashUser = PasswordUtil.hashear("user123");
                String insertSql = "INSERT INTO usuarios (username, password_hash, rol) "
                        + "VALUES ('enfermera1', '" + hashUser + "', 'USER')";
                try (Statement s2 = connection.createStatement()) {
                    s2.execute(insertSql);
                }
            }
        }
    }

    private void insertarPacientesEjemplo() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM pacientes";
        try (Statement stmt = connection.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Usar usuario admin para las operaciones
                Usuario usuarioAdmin = new Usuario(1, "admin", "admin123", Usuario.Rol.ADMIN);
                
                Paciente[] pacientes = {
                    new Paciente(0, "María", "García", "1001234567", "3101234567", "maria@gmail.com", "1985-03-12", "Hipertensión arterial"),
                    new Paciente(0, "Carlos", "Rodríguez", "1009876543", "3209876543", "carlos@gmail.com", "1990-07-25", "Diabetes tipo 2"),
                    new Paciente(0, "Laura", "Martínez", "1005551234", "3005551234", "laura@gmail.com", "2000-11-08", "Asma bronquial")
                };
                
                for (Paciente paciente : pacientes) {
                    pacienteService.registrar(paciente, usuarioAdmin);
                }
            }
        }
    }
}