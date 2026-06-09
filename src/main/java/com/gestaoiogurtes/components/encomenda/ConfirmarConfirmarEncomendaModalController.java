package com.gestaoiogurtes.components.encomenda;

import com.gestaoiogurtes.services.EncomendaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class ConfirmarConfirmarEncomendaModalController {

    @FXML private Label lblErro;
    @FXML private VBox erroContainer;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private EncomendaService service;
    private String encomendaId;
    private Runnable onSuccess;

    public static void show(String encomendaId, EncomendaService service, Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConfirmarConfirmarEncomendaModalController.class
                            .getResource("/fxml/components/encomenda/ConfirmarConfirmarEncomendaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Confirmar Encomenda");
            stage.setScene(new Scene(root));

            ConfirmarConfirmarEncomendaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.encomendaId = encomendaId;
            ctrl.onSuccess = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lblErro.setText("");
        erroContainer.setVisible(false);
        erroContainer.setManaged(false);
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
        erroContainer.setVisible(false);
        erroContainer.setManaged(false);

        service.confirmar(encomendaId, state -> {
            if (state.isLoading()) {
                btnConfirmar.setText("A confirmar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else if (state.isError()) {
                btnConfirmar.setDisable(false);
                btnCancelar.setDisable(false);
                btnConfirmar.setText("Confirmar Encomenda");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText("Erro: " + erro);
                erroContainer.setVisible(true);
                erroContainer.setManaged(true);
            }
        });
    }
}
