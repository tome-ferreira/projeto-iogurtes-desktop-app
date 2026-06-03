package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.services.ProdutoFinalService;
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

public class EliminarComposicaoModalController {

    @FXML private Label lblMensagem;
    @FXML private Label lblErro;
    @FXML private Button btnCancelar;
    @FXML private Button btnRemover;

    private Stage dialogStage;
    private ProdutoFinalService service;
    private String produtoId;
    private String composicaoId;
    private Runnable onSuccess;

    public static void show(
            String produtoId,
            String composicaoId,
            String materiaNome,
            ProdutoFinalService service,
            Window owner,
            Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EliminarComposicaoModalController.class
                            .getResource("/fxml/components/produtoFinal/EliminarComposicaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Remover da Composição");
            stage.setScene(new Scene(root));

            EliminarComposicaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.produtoId = produtoId;
            ctrl.composicaoId = composicaoId;
            ctrl.onSuccess = onSuccess;

            ctrl.lblMensagem.setText("Tem a certeza que pretende remover '" + materiaNome + "' da composição?");

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
    private void handleRemover() {
        lblErro.setText("");
        btnRemover.setDisable(true);
        btnRemover.setText("A remover...");
        btnCancelar.setDisable(true);

        service.removeComposicao(produtoId, composicaoId, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) onSuccess.run();
            } else if (state.isError()) {
                lblErro.setText(state.getErrorMessage());
                btnRemover.setDisable(false);
                btnRemover.setText("Remover");
                btnCancelar.setDisable(false);
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
