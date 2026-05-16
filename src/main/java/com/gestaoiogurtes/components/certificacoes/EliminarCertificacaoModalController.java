package com.gestaoiogurtes.components.certificacoes;

import com.gestaoiogurtes.models.certificacao.CertificacaoResponse;
import com.gestaoiogurtes.services.CertificacaoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class EliminarCertificacaoModalController {

    private CertificacaoResponse certificacao;
    private CertificacaoService service;
    private Consumer<String> onSuccess;

    @FXML private Label lblMensagem;
    @FXML private Label erroFeedback;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;

    public static void show(CertificacaoResponse certificacao, CertificacaoService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarCertificacaoModalController.class.getResource("/fxml/components/certificacoes/EliminarCertificacaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Certificação");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EliminarCertificacaoModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setDependencies(certificacao, service, onSuccess);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setDependencies(CertificacaoResponse certificacao, CertificacaoService service, Consumer<String> onSuccess) {
        this.certificacao = certificacao;
        this.service = service;
        this.onSuccess = onSuccess;

        lblMensagem.setText("Tens a certeza que pretendes eliminar a certificação \"" + certificacao.nome + "\"?\nEsta ação não pode ser desfeita.");
    }

    @FXML
    public void initialize() {
        erroFeedback.setText("");
    }

    @FXML
    private void handleEliminar() {
        erroFeedback.setText("");
        setLoading(true);

        service.delete(certificacao.id.toString(), state -> {
            switch (state.getStatus()) {
                case SUCCESS -> {
                    setLoading(false);
                    dialogStage.close();
                    if (onSuccess != null) {
                        onSuccess.accept("Certificação eliminada com sucesso!");
                    }
                }
                case ERROR -> {
                    setLoading(false);
                    erroFeedback.setText("Erro da API: " + state.getErrorMessage());
                }
                default -> {} 
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private void setLoading(boolean loading) {
        btnEliminar.setDisable(loading);
        btnCancelar.setDisable(loading);
        if (loading) {
            btnEliminar.setText("A eliminar...");
        } else {
            btnEliminar.setText("Eliminar");
        }
    }
}
