package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

public class DetalhesFornecedorModalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtNif;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtMorada;
    @FXML private TextField txtCidade;
    @FXML private TextField txtTipo;

    private Stage dialogStage;

    public static void show(FornecedorResponse fornecedor, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesFornecedorModalController.class
                    .getResource("/fxml/components/fornecedor/DetalhesFornecedorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Fornecedor");
            stage.setScene(new Scene(root));

            DetalhesFornecedorModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.setFornecedor(fornecedor);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFornecedor(FornecedorResponse f) {
        txtNome.setText(f.nome != null ? f.nome : "—");
        txtNif.setText(f.nif != null ? f.nif : "—");
        txtEmail.setText(f.email != null ? f.email : "—");
        txtTelefone.setText(f.telefone != null ? f.telefone : "—");
        txtMorada.setText(f.morada != null ? f.morada : "—");
        txtCidade.setText(f.cidade != null ? f.cidade : "—");
        
        if (f.tipo != null && f.tipo.nome != null) {
            txtTipo.setText(f.tipo.nome);
        } else {
            txtTipo.setText("Sem Tipo");
        }
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
