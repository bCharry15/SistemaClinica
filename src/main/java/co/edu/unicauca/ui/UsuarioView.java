package co.edu.unicauca.ui;

import co.edu.unicauca.model.Usuario;
import co.edu.unicauca.service.IAuthService;
import co.edu.unicauca.service.IPacienteService;
import co.edu.unicauca.service.IUsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UsuarioView {

    private final Usuario usuarioActivo;
    private final IAuthService authService;
    private final IPacienteService pacienteService;
    private final IUsuarioService usuarioService;
    private final Stage stage;
    private TableView<Usuario> tabla;
    private ObservableList<Usuario> datos;

    public UsuarioView(Usuario usuarioActivo, IAuthService authService, IPacienteService pacienteService, 
                       IUsuarioService usuarioService, Stage stage) {
        this.usuarioActivo = usuarioActivo;
        this.authService = authService;
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
        this.stage = stage;
    }

    public void mostrar() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0d1117;");

        root.setTop(crearHeader());
        root.setCenter(crearCuerpo());

        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Sistema Clínica — Gestión de Usuarios");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        cargarDatos();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private HBox crearHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setSpacing(12);
        header.setStyle(
            "-fx-background-color: #161b22;" +
            "-fx-border-color: #30363d;" +
            "-fx-border-width: 0 0 1 0;"
        );

        Text icono = new Text("📋");
        icono.setFont(Font.font(22));

        Text titulo = new Text("Gestión de Usuarios");
        titulo.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        titulo.setFill(Color.web("#e6edf3"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblUsuario = new Label("👤 " + usuarioActivo.getUsername() + "  [" + usuarioActivo.getRol() + "]");
        lblUsuario.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");

        Button btnVolver = new Button("← Volver");
        btnVolver.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #58a6ff;" +
            "-fx-border-color: #58a6ff;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 5 12 5 12;"
        );
        btnVolver.setOnAction(e -> new PacienteView(usuarioActivo, authService, stage, pacienteService, usuarioService).mostrar());

        header.getChildren().addAll(icono, titulo, spacer, lblUsuario, btnVolver);
        return header;
    }

    // ── Cuerpo principal ──────────────────────────────────────────────────────
    private VBox crearCuerpo() {
        VBox cuerpo = new VBox(16);
        cuerpo.setPadding(new Insets(24));

        cuerpo.getChildren().addAll(crearBarraAcciones(), crearTabla());
        return cuerpo;
    }

    // ── Barra de acciones ─────────────────────────────────────────────────────
    private HBox crearBarraAcciones() {
        HBox barra = new HBox(10);
        barra.setAlignment(Pos.CENTER_LEFT);

        Text tituloSeccion = new Text("Lista de Usuarios");
        tituloSeccion.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        tituloSeccion.setFill(Color.web("#e6edf3"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevo = crearBoton("+ Nuevo Usuario", "#238636", "#2ea043");
        Button btnEliminar = crearBoton("🗑 Eliminar", "#b62324", "#da3633");

        btnNuevo.setOnAction(e -> abrirFormularioNuevoUsuario());
        btnEliminar.setOnAction(e -> eliminarUsuario());

        barra.getChildren().addAll(tituloSeccion, spacer, btnNuevo, btnEliminar);
        return barra;
    }

    // ── Tabla ─────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private TableView<Usuario> crearTabla() {
        tabla = new TableView<>();
        tabla.setStyle(
            "-fx-background-color: #161b22;" +
            "-fx-border-color: #30363d;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-table-cell-border-color: #21262d;"
        );
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Usuario, Integer> colId = crearColumna("ID", "id", 50);
        TableColumn<Usuario, String> colUsername = crearColumna("Usuario", "username", 150);
        TableColumn<Usuario, String> colRol = crearColumna("Rol", "rol", 100);

        tabla.getColumns().addAll(colId, colUsername, colRol);

        datos = FXCollections.observableArrayList();
        tabla.setItems(datos);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.4));
        shadow.setRadius(15);
        tabla.setEffect(shadow);

        return tabla;
    }

    private <T> TableColumn<Usuario, T> crearColumna(String titulo, String propiedad, int ancho) {
        TableColumn<Usuario, T> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        col.setPrefWidth(ancho);
        col.setStyle("-fx-alignment: CENTER-LEFT;");
        return col;
    }

    // ── Formulario nuevo usuario ──────────────────────────────────────────────
    private void abrirFormularioNuevoUsuario() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear Nuevo Usuario");
        dialog.setHeaderText("Registrar un nuevo usuario del sistema");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nombre de usuario");
        
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña (mín. 6 caracteres)");
        
        ComboBox<Usuario.Rol> cmbRol = new ComboBox<>();
        cmbRol.setItems(FXCollections.observableArrayList(Usuario.Rol.values()));
        cmbRol.setValue(Usuario.Rol.USER);

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(new Label("Rol:"), 0, 2);
        grid.add(cmbRol, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    String username = txtUsername.getText().trim();
                    String password = txtPassword.getText();
                    Usuario.Rol rol = cmbRol.getValue();

                    usuarioService.crear(username, password, rol, usuarioActivo);
                    cargarDatos();
                    mostrarAlerta("Usuario creado exitosamente.", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    mostrarAlerta("Error: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // ── Eliminar usuario ──────────────────────────────────────────────────────
    private void eliminarUsuario() {
        Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un usuario para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        if (seleccionado.getId() == usuarioActivo.getId()) {
            mostrarAlerta("No puedes eliminar tu propia cuenta.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar a " + seleccionado.getUsername() + "?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    usuarioService.eliminar(seleccionado.getId(), usuarioActivo);
                    cargarDatos();
                    mostrarAlerta("Usuario eliminado correctamente.", Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    mostrarAlerta("Error: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // ── Cargar datos ──────────────────────────────────────────────────────────
    public void cargarDatos() {
        datos.setAll(usuarioService.listarTodos());
    }

    // ── Utilidades ────────────────────────────────────────────────────────────
    private Button crearBoton(String texto, String colorNormal, String colorHover) {
        Button btn = new Button(texto);
        String estiloBase = "-fx-background-color: %s; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-cursor: hand; " +
                            "-fx-font-size: 12px; -fx-padding: 7 14 7 14;";
        btn.setStyle(String.format(estiloBase, colorNormal));
        btn.setOnMouseEntered(e -> btn.setStyle(String.format(estiloBase, colorHover)));
        btn.setOnMouseExited(e -> btn.setStyle(String.format(estiloBase, colorNormal)));
        return btn;
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
