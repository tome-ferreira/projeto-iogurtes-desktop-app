package com.gestaoiogurtes.components.fornecedorestipo;

import com.gestaoiogurtes.models.fornecedortipo.CreateFornecedorTipoRequest;
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

public class CriarFornecedorTipoModalController {

    private FornecedorTipoService service;

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private Label lblErro;
    @FXML private Button btnCriar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(FornecedorTipoService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarFornecedorTipoModalController.class.getResource("/fxml/components/fornecedorestipo/CriarFornecedorTipoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Novo Tipo de Fornecedor");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CriarFornecedorTipoModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);

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

    @FXML
    public void initialize() {
        // Any specific initialization here
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");

        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String descricao = txtDescricao.getText() == null ? "" : txtDescricao.getText().trim();

        if (nome.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        var request = new CreateFornecedorTipoRequest(
                nome,
                descricao.isEmpty() ? null : descricao
        );

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.create(request, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Tipo de fornecedor \"" + request.nome + "\" criado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Tipo");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
