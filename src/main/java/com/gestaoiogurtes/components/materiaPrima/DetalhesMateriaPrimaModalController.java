package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
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

public class DetalhesMateriaPrimaModalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtUnidade;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtStockAtual;
    @FXML private TextField txtTipo;
    @FXML private TextField txtEstado;
    
    private Stage dialogStage;

    public static void show(MateriaPrimaResponse entidade, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DetalhesMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/DetalhesMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes da Matéria Prima");
            stage.setScene(new Scene(root));

            DetalhesMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.setEntidade(entidade);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setEntidade(MateriaPrimaResponse p) {
        txtNome.setText(p.nome != null ? p.nome : "—");
        txtUnidade.setText(p.unidade != null ? p.unidade : "—");
        txtStockMinimo.setText(p.stockMinimo != null ? String.valueOf(p.stockMinimo) : "—");
        txtStockAtual.setText(p.stockAtual != null ? String.valueOf(p.stockAtual) : "—");
        txtTipo.setText(p.tipo != null && p.tipo.nome != null ? p.tipo.nome : "Sem tipo associado");
        
        if (p.isActive != null) {
            txtEstado.setText(p.isActive ? "Ativo" : "Inativo");
        } else {
            txtEstado.setText("—");
        }
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
