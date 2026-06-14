package com.gestaoiogurtes.components.tipomateriaPrima;

import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import com.gestaoiogurtes.services.TipoMateriaPrimaService;
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

public class EliminarTipoMateriaPrimaModalController {

    private TipoMateriaPrimaService service;
    private TipoMateriaPrimaResponse tipoMateriaPrima;

    @FXML private Label lblMensagem;
    @FXML private Label lblErro;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(TipoMateriaPrimaResponse tipoMateriaPrima, TipoMateriaPrimaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarTipoMateriaPrimaModalController.class.getResource("/fxml/components/tipomateriaPrima/EliminarTipoMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Tipo de Matéria Prima");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EliminarTipoMateriaPrimaModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);
            controller.setTipoMateriaPrima(tipoMateriaPrima);

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

    private void setService(TipoMateriaPrimaService service) {
        this.service = service;
    }

    private void setTipoMateriaPrima(TipoMateriaPrimaResponse tipoMateriaPrima) {
        this.tipoMateriaPrima = tipoMateriaPrima;
        lblMensagem.setText("Tem a certeza que deseja eliminar o tipo de matéria prima \"" + tipoMateriaPrima.nome + "\"?\nEsta ação não pode ser desfeita.");
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

        service.delete(tipoMateriaPrima.id.toString(), state -> {
            if (state.isLoading()) {
                btnEliminar.setText("A eliminar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Tipo de Matéria Prima \"" + tipoMateriaPrima.nome + "\" eliminado com sucesso.").replaceAll("\\R", " ").strip());
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
