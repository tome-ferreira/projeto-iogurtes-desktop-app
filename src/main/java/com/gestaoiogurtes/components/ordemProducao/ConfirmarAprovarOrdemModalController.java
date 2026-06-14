package com.gestaoiogurtes.components.ordemProducao;

import com.gestaoiogurtes.services.OrdemProducaoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class ConfirmarAprovarOrdemModalController {

    @FXML private Label  lblErro;
    @FXML private ScrollPane scrollErro;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private OrdemProducaoService service;
    private String ordemId;
    private Consumer<String> onSuccess;

    public static void show(String ordemId,
                            OrdemProducaoService service,
                            Window owner,
                            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConfirmarAprovarOrdemModalController.class
                            .getResource("/fxml/components/ordemProducao/ConfirmarAprovarOrdemModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Aprovar Ordem de Produção");
            stage.setScene(new Scene(root));

            ConfirmarAprovarOrdemModalController ctrl = loader.getController();
            ctrl.dialogStage  = stage;
            ctrl.service      = service;
            ctrl.ordemId      = ordemId;
            ctrl.onSuccess    = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        mostrarErro(null);
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleConfirmar() {
        btnConfirmar.setDisable(true);
        btnCancelar.setDisable(true);
        mostrarErro(null);

        service.aprovar(ordemId, state -> {
            if (state.isLoading()) {
                btnConfirmar.setText("A aprovar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Ordem de produção aprovada com sucesso.");
                }
            } else if (state.isError()) {
                btnConfirmar.setDisable(false);
                btnCancelar.setDisable(false);
                btnConfirmar.setText("Aprovar");
                String erro = state.getErrorMessage() != null
                        ? state.getErrorMessage() : "Erro desconhecido";
                mostrarErro("Erro:\n" + erro);
            }
        });
    }

    private void mostrarErro(String msg) {
        boolean hasError = msg != null && !msg.trim().isEmpty();
        lblErro.setText(hasError ? msg : "");
        if (scrollErro != null) {
            scrollErro.setVisible(hasError);
            scrollErro.setManaged(hasError);
        }
    }
}
