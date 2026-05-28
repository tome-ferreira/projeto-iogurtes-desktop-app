package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.services.FornecedorService;
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

/**
 * Modal de confirmação para remover uma certificação de um fornecedor.
 * Chama DELETE /fornecedor/certificacoes/{fornecedorCertificacaoId}.
 */
public class EliminarCertificacaoModalController {

    @FXML private Label  lblMensagem;
    @FXML private Label  lblErro;
    @FXML private Button btnRemover;
    @FXML private Button btnCancelar;

    private Stage            dialogStage;
    private FornecedorService service;
    private String           fornecedorCertificacaoId;
    private Runnable         onSuccess;

    // ── Abertura ────────────────────────────────────────────────────────────

    public static void show(String fornecedorCertificacaoId, String certificacaoNome,
                            FornecedorService service, Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EliminarCertificacaoModalController.class
                            .getResource("/fxml/components/fornecedor/EliminarCertificacaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Remover Certificação");
            stage.setScene(new Scene(root));

            EliminarCertificacaoModalController ctrl = loader.getController();
            ctrl.dialogStage              = stage;
            ctrl.service                  = service;
            ctrl.fornecedorCertificacaoId = fornecedorCertificacaoId;
            ctrl.onSuccess                = onSuccess;
            ctrl.lblMensagem.setText(
                    "Tem a certeza que pretende remover a certificação \""
                    + (certificacaoNome != null ? certificacaoNome : "desconhecida") + "\"?");

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Inicialização ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        lblErro.setText("");
    }

    // ── Handlers ────────────────────────────────────────────────────────────

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleRemover() {
        btnRemover.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.removeCertificacao(fornecedorCertificacaoId, state -> {
            if (state.isLoading()) {
                btnRemover.setText("A remover...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.run();
            } else if (state.isError()) {
                btnRemover.setDisable(false);
                btnCancelar.setDisable(false);
                btnRemover.setText("Remover");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
