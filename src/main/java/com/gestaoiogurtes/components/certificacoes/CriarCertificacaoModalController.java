package com.gestaoiogurtes.components.certificacoes;

import com.gestaoiogurtes.models.certificacao.CreateCertificacaoRequest;
import com.gestaoiogurtes.services.CertificacaoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class CriarCertificacaoModalController {

    private CertificacaoService service;
    private Consumer<String> onSuccess;

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private Label erroNome;
    @FXML private Button btnGravar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;

    public static void show(CertificacaoService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarCertificacaoModalController.class.getResource("/fxml/components/certificacoes/CriarCertificacaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Nova Certificação");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CriarCertificacaoModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setDependencies(service, onSuccess);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setDependencies(CertificacaoService service, Consumer<String> onSuccess) {
        this.service = service;
        this.onSuccess = onSuccess;
    }

    @FXML
    public void initialize() {
        limparErros();
        Platform.runLater(() -> txtNome.requestFocus());
    }

    @FXML
    private void handleGravar() {
        limparErros();
        if (!validarFormulario()) return;

        var request = new CreateCertificacaoRequest(
                txtNome.getText().trim(),
                txtDescricao.getText() != null ? txtDescricao.getText().trim() : ""
        );

        setLoading(true);

        service.create(request, state -> {
            switch (state.getStatus()) {
                case SUCCESS -> {
                    setLoading(false);
                    dialogStage.close();
                    if (onSuccess != null) {
                        onSuccess.accept("Certificação criada com sucesso!");
                    }
                }
                case ERROR -> {
                    setLoading(false);
                    erroNome.setText("Erro da API: " + state.getErrorMessage());
                }
                default -> {} 
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private boolean validarFormulario() {
        boolean valido = true;

        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            erroNome.setText("O nome é obrigatório");
            valido = false;
        }

        return valido;
    }

    private void limparErros() {
        erroNome.setText("");
    }

    private void setLoading(boolean loading) {
        btnGravar.setDisable(loading);
        btnCancelar.setDisable(loading);
        txtNome.setDisable(loading);
        txtDescricao.setDisable(loading);
        if (loading) {
            btnGravar.setText("A gravar...");
        } else {
            btnGravar.setText("Gravar");
        }
    }
}
