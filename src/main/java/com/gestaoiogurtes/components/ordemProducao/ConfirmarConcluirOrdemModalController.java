package com.gestaoiogurtes.components.ordemProducao;

import com.gestaoiogurtes.services.OrdemProducaoService;
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

public class ConfirmarConcluirOrdemModalController {

    @FXML private Label  lblErro;
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
                    ConfirmarConcluirOrdemModalController.class
                            .getResource("/fxml/components/ordemProducao/ConfirmarConcluirOrdemModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Concluir Ordem de Produção");
            stage.setScene(new Scene(root));

            ConfirmarConcluirOrdemModalController ctrl = loader.getController();
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
        lblErro.setText("");
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleConfirmar() {
        btnConfirmar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.concluir(ordemId, state -> {
            if (state.isLoading()) {
                btnConfirmar.setText("A concluir...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Ordem de produção concluída com sucesso.");
                }
            } else if (state.isError()) {
                btnConfirmar.setDisable(false);
                btnCancelar.setDisable(false);
                btnConfirmar.setText("Sim, Concluir Ordem");
                String erro = state.getErrorMessage() != null
                        ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
