package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
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
import java.util.function.Consumer;

public class EliminarProdutoFinalModalController {

    @FXML
    private Label lblMensagem;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnEliminar;

    private Stage dialogStage;
    private ProdutoFinalService service;
    private Consumer<String> onSuccess;
    private String idToEliminar;

    public static void show(
            ProdutoFinalResponse produto,
            ProdutoFinalService service,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EliminarProdutoFinalModalController.class
                            .getResource("/fxml/components/produtoFinal/EliminarProdutoFinalModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Produto Final");
            stage.setScene(new Scene(root));

            EliminarProdutoFinalModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.onSuccess = onSuccess;
            ctrl.setEntidade(produto);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setEntidade(ProdutoFinalResponse p) {
        this.idToEliminar = p.id != null ? p.id.toString() : null;
        String nome = p.nome != null ? p.nome : "este produto final";
        lblMensagem.setText("Tem a certeza que deseja eliminar \"" + nome + "\"?");
    }

    @FXML
    private void handleEliminar() {
        if (idToEliminar == null)
            return;

        btnEliminar.setDisable(true);
        btnEliminar.setText("A eliminar...");
        lblErro.setText("");

        service.delete(idToEliminar, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Produto Final eliminado com sucesso.");
                }
            } else if (state.isError()) {
                btnEliminar.setDisable(false);
                btnEliminar.setText("Eliminar");
                lblErro.setText("Erro: " + state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
