package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.bll.model.Empresa;
import com.gestaoiogurtes.bll.model.User;
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
import java.util.stream.Collectors;

public class EditarUtilizadorModalController {

    private final IUserService userService = ServiceLocator.userService();
    private final IEmpresaService empresaService = ServiceLocator.empresaService();

    @FXML private TextField txtNome;
    @FXML private ComboBox<TurnoTipo> comboTurno;
    @FXML private ComboBox<UserRoleType> comboRole;
    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private VBox boxTurno;
    @FXML private VBox boxEmpresa;

    @FXML private Label lblErroNome;
    @FXML private Label lblErroTurno;
    @FXML private Label lblErroRole;
    @FXML private Label lblErroEmpresa;
    @FXML private Label lblErro;

    private Stage dialogStage;
    private Runnable onSuccess;
    private User user;
    private boolean isInitialising = true;

    public static void show(User user, Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarUtilizadorModalController.class.getResource("/fxml/components/utilizadores/EditarUtilizadorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Utilizador");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EditarUtilizadorModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setUser(user);

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

        comboEmpresa.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Empresa e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? "Selecione uma empresa..." : e.getNomeEmpresa());
            }
        });
        comboEmpresa.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Empresa e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? "Selecione uma empresa..." : e.getNomeEmpresa());
            }
        });
        comboEmpresa.setItems(FXCollections.observableArrayList(empresaService.getAll()));

        comboRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isInitialising || newVal == null) return;
            boolean isEmpresa = (newVal == UserRoleType.EMPRESA);
            
            boxTurno.setVisible(!isEmpresa);
            boxTurno.setManaged(!isEmpresa);
            
            boxEmpresa.setVisible(isEmpresa);
            boxEmpresa.setManaged(isEmpresa);

            // Clear values of fields that become hidden
            if (isEmpresa) {
                comboTurno.setValue(null);
            } else {
                comboEmpresa.setValue(null);
            }
        });
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

    private void setUser(User user) {
        this.user = user;
        isInitialising = true;
        txtNome.setText(user.getNome());

        if (user.getTurno() != null) {
            comboTurno.setValue(user.getTurno());
        }

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            comboRole.setValue(user.getRoles().get(0).getRole());
        }

        if (user.getEmpresa() != null) {
            comboEmpresa.setValue(user.getEmpresa());
        }

        // Aplicar a visibilidade baseada na role atual sem disparar side effects indesejados
        boolean isEmpresa = (comboRole.getValue() == UserRoleType.EMPRESA);
        boxTurno.setVisible(!isEmpresa);
        boxTurno.setManaged(!isEmpresa);
        boxEmpresa.setVisible(isEmpresa);
        boxEmpresa.setManaged(isEmpresa);

        isInitialising = false;
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleGuardar() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        esconderErro(lblErroNome);
        esconderErro(lblErroTurno);
        esconderErro(lblErroRole);
        esconderErro(lblErroEmpresa);

        boolean temErros = false;

        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        if (nome.isEmpty()) {
            mostrarErro(lblErroNome, "Nome é obrigatório.");
            temErros = true;
        }

        UserRoleType role = comboRole.getValue();
        if (role == null) {
            mostrarErro(lblErroRole, "Selecione um papel (Role).");
            temErros = true;
        }

        if (role != null) {
            if (role == UserRoleType.EMPRESA) {
                if (comboEmpresa.getValue() == null) {
                    mostrarErro(lblErroEmpresa, "Empresa é obrigatória para este papel.");
                    temErros = true;
                }
            } else {
                if (comboTurno.getValue() == null) {
                    mostrarErro(lblErroTurno, "Turno é obrigatório para este papel.");
                    temErros = true;
                }
            }
        }

        if (temErros) {
            return;
        }

        try {
            boolean isCliente = (role == UserRoleType.EMPRESA);
            String turnoStr = (!isCliente && comboTurno.getValue() != null) ? comboTurno.getValue().name() : null;

            // TODO: implementar update empresa (a API não suporta no momento)
            // Se isCliente = true e uma empresa for selecionada, ela deve ser persistida.
            
            userService.updateUser(user.getId(), nome, turnoStr, List.of(role.name()));

            onSuccess.run();
            dialogStage.close();
        } catch (Exception e) {
            lblErro.setText("Erro ao salvar: " + e.getMessage());
            lblErro.setVisible(true);
            lblErro.setManaged(true);
        }
    }

    private void mostrarErro(Label lbl, String mensagem) {
        lbl.setText(mensagem);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private void esconderErro(Label lbl) {
        lbl.setVisible(false);
        lbl.setManaged(false);
    }
}
