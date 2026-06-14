package com.gestaoiogurtes.components.fornecedorestipo;

import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class DetalhesFornecedorTipoModalController {

    private FornecedorTipoResponse tipo;

    @FXML
    private TextField txtNome;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private Button btnFechar;

    private Stage dialogStage;

    public static void show(FornecedorTipoResponse tipo, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesFornecedorTipoModalController.class
                    .getResource("/fxml/components/fornecedorestipo/DetalhesFornecedorTipoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Tipo de Fornecedor");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            DetalhesFornecedorTipoModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setTipo(tipo);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setTipo(FornecedorTipoResponse tipo) {
        this.tipo = tipo;
        txtNome.setText(tipo.nome != null ? tipo.nome : "");
        txtDescricao.setText(tipo.descricao != null ? tipo.descricao : "");

        txtNome.setEditable(false);
        txtDescricao.setEditable(false);
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
