package com.gestaoiogurtes.components.moeda;

import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.services.MoedaService;
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

public class EliminarMoedaModalController {

    private MoedaService service;
    private MoedaResponse moeda;

    @FXML private Label lblMensagem;
    @FXML private Label lblErro;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(MoedaResponse moeda, MoedaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarMoedaModalController.class.getResource("/fxml/components/moeda/EliminarMoedaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Moeda");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EliminarMoedaModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);
            controller.setMoeda(moeda);

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

    private void setService(MoedaService service) {
        this.service = service;
    }

    private void setMoeda(MoedaResponse moeda) {
        this.moeda = moeda;
        lblMensagem.setText("Tem a certeza que deseja eliminar a moeda \"" + moeda.nome + "\" (" + moeda.codigo + ")?");
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleEliminar() {
        btnEliminar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.delete(moeda.id.toString(), state -> {
            if (state.isLoading()) {
                btnEliminar.setText("A eliminar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Moeda \"" + moeda.nome + "\" eliminada com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnEliminar.setDisable(false);
                btnCancelar.setDisable(false);
                btnEliminar.setText("Eliminar");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
