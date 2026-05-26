package com.gestaoiogurtes.components.tipomateriaPrima;

import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import com.gestaoiogurtes.models.tipoMateriaPrima.UpdateTipoMateriaPrimaRequest;
import com.gestaoiogurtes.services.TipoMateriaPrimaService;
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
import java.util.Locale;
import java.util.function.Consumer;

public class EditarTipoMateriaPrimaModalController {

    private TipoMateriaPrimaService service;
    private TipoMateriaPrimaResponse tipoMateriaPrima;

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtTaxaIva;
    @FXML private Label lblErro;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(TipoMateriaPrimaResponse tipoMateriaPrima, TipoMateriaPrimaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarTipoMateriaPrimaModalController.class.getResource("/fxml/components/tipomateriaPrima/EditarTipoMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Tipo de Matéria Prima");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EditarTipoMateriaPrimaModalController controller = loader.getController();
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
        preencherDados();
    }

    private void preencherDados() {
        if (tipoMateriaPrima != null) {
            txtNome.setText(tipoMateriaPrima.nome);
            txtDescricao.setText(tipoMateriaPrima.descricao);
            txtTaxaIva.setText(tipoMateriaPrima.iva != null ? String.format(Locale.US, "%.2f", tipoMateriaPrima.iva) : "");
        }
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");

        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String descricao = txtDescricao.getText() == null ? "" : txtDescricao.getText().trim();
        String taxaIvaStr = txtTaxaIva.getText() == null ? "" : txtTaxaIva.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty() || taxaIvaStr.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        Double taxaIva;
        try {
            taxaIvaStr = taxaIvaStr.replace(",", ".");
            taxaIva = Double.parseDouble(taxaIvaStr);
            if (taxaIva < 0) {
                lblErro.setText("A taxa de IVA não pode ser negativa.");
                return;
            }
        } catch (NumberFormatException e) {
            lblErro.setText("A taxa de IVA deve ser um número válido.");
            return;
        }

        var request = new UpdateTipoMateriaPrimaRequest(
                nome,
                descricao,
                taxaIva
        );

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.update(tipoMateriaPrima.id.toString(), request, state -> {
            if (state.isLoading()) {
                btnGuardar.setText("A guardar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Tipo de Matéria Prima \"" + request.nome + "\" atualizado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnCancelar.setDisable(false);
                btnGuardar.setText("Guardar Alterações");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
