package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import com.gestaoiogurtes.services.FornecedorService;
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

public class EliminarFornecedorModalController {

    private FornecedorService service;
    private FornecedorResponse fornecedor;

    @FXML private Label lblNomeFornecedor;
    @FXML private Label lblErro;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(
            FornecedorResponse fornecedor,
            FornecedorService service,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EliminarFornecedorModalController.class
                    .getResource("/fxml/components/fornecedor/EliminarFornecedorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Eliminar Fornecedor");
            stage.setScene(new Scene(root));

            EliminarFornecedorModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.onSuccess = onSuccess;
            ctrl.service = service;
            ctrl.setFornecedor(fornecedor);

            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setFornecedor(FornecedorResponse f) {
        this.fornecedor = f;
        lblNomeFornecedor.setText(f.nome != null ? f.nome : "Fornecedor desconhecido");
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
    private void handleEliminar() {
        btnEliminar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.delete(fornecedor.id.toString(), state -> {
            if (state.isLoading()) {
                btnEliminar.setText("A eliminar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Fornecedor \"" + fornecedor.nome + "\" eliminado com sucesso.").replaceAll("\\R", " ").strip());
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
