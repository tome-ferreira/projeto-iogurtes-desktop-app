package com.gestaoiogurtes.components.materiaPrima;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class FornecedoresEmBreveModalController {

    private Stage dialogStage;

    public static void show(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FornecedoresEmBreveModalController.class
                            .getResource("/fxml/components/materiaPrima/FornecedoresEmBreveModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Em Breve");
            stage.setScene(new Scene(root));

            FornecedoresEmBreveModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
