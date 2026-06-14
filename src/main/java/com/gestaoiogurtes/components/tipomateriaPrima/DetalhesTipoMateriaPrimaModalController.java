package com.gestaoiogurtes.components.tipomateriaPrima;

import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DetalhesTipoMateriaPrimaModalController {

    @FXML
    private TextField txtNome;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private TextField txtTaxaIva;
    @FXML
    private TextField txtCriadoEm;
    @FXML
    private TextField txtAtivo;
    @FXML
    private Button btnFechar;

    private Stage dialogStage;

    public static void show(TipoMateriaPrimaResponse tipoMateriaPrima, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesTipoMateriaPrimaModalController.class
                    .getResource("/fxml/components/tipomateriaPrima/DetalhesTipoMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Detalhes do Tipo de Matéria Prima");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            DetalhesTipoMateriaPrimaModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.preencherDados(tipoMateriaPrima);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void preencherDados(TipoMateriaPrimaResponse t) {
        txtNome.setText(t.nome != null ? t.nome : "—");
        txtDescricao.setText(t.descricao != null ? t.descricao : "—");
        txtTaxaIva.setText(t.iva != null ? t.iva + "%" : "—");

        if (t.isActive != null) {
            txtAtivo.setText(t.isActive ? "Sim" : "Não");
        } else {
            txtAtivo.setText("—");
        }

        if (t.createdAt != null) {
            txtCriadoEm.setText(t.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } else {
            txtCriadoEm.setText("—");
        }

        txtNome.setEditable(false);
        txtDescricao.setEditable(false);
        txtTaxaIva.setEditable(false);
        txtAtivo.setEditable(false);
        txtCriadoEm.setEditable(false);
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }
}
