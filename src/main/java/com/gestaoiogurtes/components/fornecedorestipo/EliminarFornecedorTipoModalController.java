package com.gestaoiogurtes.components.fornecedorestipo;

import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.services.FornecedorTipoService;
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

public class EliminarFornecedorTipoModalController {

    private FornecedorTipoService service;
    private FornecedorTipoResponse tipo;

    @FXML
    private Label lblAviso;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(FornecedorTipoResponse tipo, FornecedorTipoService service, Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarFornecedorTipoModalController.class
                    .getResource("/fxml/components/fornecedorestipo/EliminarFornecedorTipoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Confirmar Eliminação");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EliminarFornecedorTipoModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);
            controller.setTipo(tipo);

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

    private void setService(FornecedorTipoService service) {
        this.service = service;
    }

    private void setTipo(FornecedorTipoResponse tipo) {
        this.tipo = tipo;
        lblAviso.setText("Esta acção irá eliminar permanentemente o tipo de fornecedor\n\"" +
                tipo.nome + "\" e todos os dados associados.\n\n" +
                "Esta operação não pode ser revertida.");
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleEliminar() {
        btnEliminar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        service.delete(tipo.id.toString(), state -> {
            if (state.isLoading()) {
                btnEliminar.setText("A eliminar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Tipo de fornecedor \"" + tipo.nome + "\" eliminado com sucesso.")
                        .replaceAll("\\R", " ").strip());
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
