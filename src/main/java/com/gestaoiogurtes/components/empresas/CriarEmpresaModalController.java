package com.gestaoiogurtes.components.empresas;

import com.gestaoiogurtes.models.empresa.CreateEmpresaRequest;
import com.gestaoiogurtes.services.EmpresaService;
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

public class CriarEmpresaModalController {

    private EmpresaService service;

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtNipc;
    @FXML
    private TextField txtTelefone;
    @FXML
    private TextField txtMorada;
    @FXML
    private TextField txtCp;
    @FXML
    private TextField txtCidade;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnCriar;
    @FXML
    private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(EmpresaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CriarEmpresaModalController.class.getResource("/fxml/components/empresas/CriarEmpresaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Nova Empresa");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CriarEmpresaModalController controller = loader.getController();
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

    private void setService(EmpresaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");

        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String nipc = txtNipc.getText() == null ? "" : txtNipc.getText().trim();
        String telefone = txtTelefone.getText() == null ? "" : txtTelefone.getText().trim();
        String morada = txtMorada.getText() == null ? "" : txtMorada.getText().trim();
        String cp = txtCp.getText() == null ? "" : txtCp.getText().trim();
        String cidade = txtCidade.getText() == null ? "" : txtCidade.getText().trim();

        if (nome.isEmpty() || nipc.isEmpty() || morada.isEmpty() || cp.isEmpty() || cidade.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        var request = new CreateEmpresaRequest(
                nome,
                nipc,
                telefone.isEmpty() ? null : telefone,
                morada,
                cp,
                cidade);

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.create(request, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(
                        ("Empresa \"" + request.nomeEmpresa + "\" criada com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Empresa");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
