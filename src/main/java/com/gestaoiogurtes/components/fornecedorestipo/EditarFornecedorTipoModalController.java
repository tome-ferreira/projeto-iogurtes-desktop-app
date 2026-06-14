package com.gestaoiogurtes.components.fornecedorestipo;

import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.models.fornecedortipo.UpdateFornecedorTipoRequest;
import com.gestaoiogurtes.services.FornecedorTipoService;
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

public class EditarFornecedorTipoModalController {

    private FornecedorTipoService service;
    private FornecedorTipoResponse tipo;

    @FXML
    private TextField txtNome;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(FornecedorTipoResponse tipo, FornecedorTipoService service, Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarFornecedorTipoModalController.class
                    .getResource("/fxml/components/fornecedorestipo/EditarFornecedorTipoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Tipo de Fornecedor");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EditarFornecedorTipoModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);
            controller.setTipo(tipo);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setOnSuccess(Consumer<String> onSuccess) {
        this.onSuccess = onSuccess;
    }

    private void setService(FornecedorTipoService service) {
        this.service = service;
    }

    private void setTipo(FornecedorTipoResponse tipo) {
        this.tipo = tipo;
        txtNome.setText(tipo.nome != null ? tipo.nome : "");
        txtDescricao.setText(tipo.descricao != null ? tipo.descricao : "");
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");

        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String descricao = txtDescricao.getText() == null ? "" : txtDescricao.getText().trim();

        if (nome.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        var request = new UpdateFornecedorTipoRequest(
                nome,
                descricao.isEmpty() ? null : descricao);

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.update(tipo.id.toString(), request, state -> {
            if (state.isLoading()) {
                btnGuardar.setText("A guardar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Tipo de fornecedor \"" + request.nome + "\" actualizado com sucesso.")
                        .replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnCancelar.setDisable(false);
                btnGuardar.setText("Guardar");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
