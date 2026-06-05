package com.gestaoiogurtes.components.encomendaMp;

import com.gestaoiogurtes.services.EncomendaMpService;
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

/**
 * Modal de confirmação para aprovar e encomendar uma encomenda de matéria prima.
 * Chama PATCH /encomendas-mp/{id}/aprovar.
 *
 * <p>O {@code onSuccess} recebe a mensagem de sucesso a apresentar na página
 * principal, via {@code EncomendaMpController.onMutacaoBemSucedida}.
 */
public class ConfirmarAprovarEncomendaModalController {

    @FXML private Label  lblErro;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Stage              dialogStage;
    private EncomendaMpService service;
    private String             encomendaId;
    private Consumer<String>   onSuccess;

    // ── Abertura ────────────────────────────────────────────────────────────

    public static void show(String encomendaId,
                            EncomendaMpService service,
                            Window owner,
                            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConfirmarAprovarEncomendaModalController.class
                            .getResource("/fxml/components/encomendaMp/ConfirmarAprovarEncomendaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Aprovar e Encomendar");
            stage.setScene(new Scene(root));

            ConfirmarAprovarEncomendaModalController ctrl = loader.getController();
            ctrl.dialogStage  = stage;
            ctrl.service      = service;
            ctrl.encomendaId  = encomendaId;
            ctrl.onSuccess    = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Inicialização ────────────────────────────────────────────────────────

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
    private void handleConfirmar() {
        btnConfirmar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.aprovar(encomendaId, state -> {
            if (state.isLoading()) {
                btnConfirmar.setText("A aprovar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Encomenda aprovada e submetida com sucesso.");
                }
            } else if (state.isError()) {
                btnConfirmar.setDisable(false);
                btnCancelar.setDisable(false);
                btnConfirmar.setText("Aprovar e Encomendar");
                String erro = state.getErrorMessage() != null
                        ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
