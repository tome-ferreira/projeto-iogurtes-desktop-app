package com.gestaoiogurtes.components.utilizadores;

import com.gestaoiogurtes.models.utilizador.UpdateGestorRequest;
import com.gestaoiogurtes.models.utilizador.UserResponse;
import com.gestaoiogurtes.services.UtilizadorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class EditarGestorModalController {

    private UtilizadorService service;
    private UserResponse utilizador;

    @FXML private TextField txtNome;
    @FXML private DatePicker dpDataAdmissao;
    @FXML private Label     lblErro;
    @FXML private Button    btnGuardar;
    @FXML private Button    btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(UserResponse utilizador, UtilizadorService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarGestorModalController.class
                    .getResource("/fxml/components/utilizadores/EditarGestorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Gestor");
            stage.setScene(new Scene(root));

            EditarGestorModalController ctrl = loader.getController();
            ctrl.dialogStage  = stage;
            ctrl.onSuccess    = onSuccess;
            ctrl.service      = service;
            ctrl.setUtilizador(utilizador);

            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setUtilizador(UserResponse u) {
        this.utilizador = u;
        txtNome.setText(u.nome != null ? u.nome : "");
        if (u.dataAdmissao != null && !u.dataAdmissao.isEmpty()) {
            dpDataAdmissao.setValue(java.time.LocalDate.parse(u.dataAdmissao));
        }
    }

    @FXML public void initialize() {}

    @FXML private void handleCancelar() { dialogStage.close(); }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String data = dpDataAdmissao.getValue() == null ? "" : dpDataAdmissao.getValue().toString();

        if (nome.isEmpty()) {
            lblErro.setText("O nome é obrigatório (*).");
            return;
        }

        var req = new UpdateGestorRequest(nome, data.isEmpty() ? null : data, null, null);

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);

        service.updateGestor(utilizador.id.toString(), req, state -> {
            if (state.isLoading()) {
                btnGuardar.setText("A guardar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Gestor \"" + req.nome + "\" atualizado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnCancelar.setDisable(false);
                btnGuardar.setText("Guardar Alterações");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
