package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.bll.model.Empresa;
import com.gestaoiogurtes.bll.model.enums.TurnoTipo;
import com.gestaoiogurtes.bll.model.enums.UserRoleType;
import com.gestaoiogurtes.services.ServiceLocator;
import com.gestaoiogurtes.services.interfaces.IEmpresaService;
import com.gestaoiogurtes.services.interfaces.IUserService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CriarUtilizadorModalController {

    private final IUserService userService = ServiceLocator.userService();
    private final IEmpresaService empresaService = ServiceLocator.empresaService();

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<TurnoTipo> comboTurno;
    @FXML private DatePicker dateAdmissao;
    @FXML private ComboBox<UserRoleType> comboRole;
    @FXML private VBox boxTurno;
    @FXML private VBox boxAdmissao;
    @FXML private VBox boxEmpresa;
    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private Label lblErro;

    private Stage dialogStage;
    private Runnable onSuccess;
    private boolean isInitialising = true;

    public static void show(Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarUtilizadorModalController.class.getResource("/fxml/components/utilizadores/CriarUtilizadorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED); // clean modern look without windows OS chrome, relying on internal styling or we can use DECORATED
            // Wait, standard DECORATED is fine and allows to move the window. I'll use UTILITY or DECORATED without resizing.
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Novo Utilizador");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CriarUtilizadorModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize() {
        isInitialising = true;

        comboTurno.setItems(FXCollections.observableArrayList(TurnoTipo.values()));
        configurarComboTurno();

        comboRole.setItems(FXCollections.observableArrayList(UserRoleType.values()));
        configurarComboRole();

        // Listener para a role
        comboRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isInitialising || newVal == null) return;
            boolean isEmpresa = (newVal == UserRoleType.EMPRESA);
            
            boxEmpresa.setVisible(isEmpresa);
            boxEmpresa.setManaged(isEmpresa);

            boxTurno.setVisible(!isEmpresa);
            boxTurno.setManaged(!isEmpresa);

            boxAdmissao.setVisible(!isEmpresa);
            boxAdmissao.setManaged(!isEmpresa);
        });

        // Preencher empresas
        comboEmpresa.setItems(FXCollections.observableArrayList(empresaService.getAll()));
        // Custom combo box rendering to show Empresa name instead of memory ref
        comboEmpresa.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Empresa e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getNomeEmpresa());
            }
        });
        comboEmpresa.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Empresa e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? "Selecione uma empresa..." : e.getNomeEmpresa());
            }
        });

        isInitialising = false;
    }

    private void configurarComboTurno() {
        comboTurno.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(TurnoTipo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : EnumDisplayHelper.getTurnoDisplayName(item));
            }
        });
        comboTurno.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(TurnoTipo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Selecione o turno..." : EnumDisplayHelper.getTurnoDisplayName(item));
            }
        });
    }

    private void configurarComboRole() {
        comboRole.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(UserRoleType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : EnumDisplayHelper.getRoleDisplayName(item));
            }
        });
        comboRole.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(UserRoleType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Selecione o papel..." : EnumDisplayHelper.getRoleDisplayName(item));
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleCriar() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        String nome = txtNome.getText();
        String email = txtEmail.getText();
        String pass = txtPassword.getText();
        UserRoleType roleParam = comboRole.getValue();
        TurnoTipo turnoParam = comboTurno.getValue();
        var data = dateAdmissao.getValue();
        Empresa empresaOpt = comboEmpresa.getValue();

        try {
            if (roleParam == null) {
                throw new IllegalArgumentException("Selecione um papel (Role)");
            }

            boolean isCliente = (roleParam == UserRoleType.EMPRESA);
            UUID empId = null;
            if (isCliente) {
                if (empresaOpt == null) throw new IllegalArgumentException("Empresa é obrigatória para clientes.");
                empId = empresaOpt.getId();
            }

            String turnoStr = (!isCliente && turnoParam != null) ? turnoParam.name() : null;
            userService.createUser(nome, email, pass, turnoStr, isCliente ? null : data, List.of(roleParam.name()), empId);
            onSuccess.run();
            dialogStage.close();
        } catch (Exception e) {
            lblErro.setText(e.getMessage());
            lblErro.setVisible(true);
            lblErro.setManaged(true);
        }
    }
}
