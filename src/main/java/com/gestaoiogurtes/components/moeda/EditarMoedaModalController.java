package com.gestaoiogurtes.components.moeda;

import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.models.moeda.UpdateMoedaRequest;
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

public class EditarMoedaModalController {

    private MoedaService service;
    private MoedaResponse moeda;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtSimbolo;
    @FXML private TextField txtTaxaConversaoEur;
    @FXML private Label lblErro;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(MoedaResponse moeda, MoedaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(EditarMoedaModalController.class.getResource("/fxml/components/moeda/EditarMoedaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Moeda");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            EditarMoedaModalController controller = loader.getController();
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
        txtCodigo.setText(moeda.codigo);
        txtCodigo.setDisable(true); // Codigo cannot be edited
        txtNome.setText(moeda.nome);
        txtSimbolo.setText(moeda.simbolo);
        txtTaxaConversaoEur.setText(moeda.taxaConversaoEur != null ? moeda.taxaConversaoEur.toString() : "");
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
        String simbolo = txtSimbolo.getText() == null ? "" : txtSimbolo.getText().trim();
        String taxaStr = txtTaxaConversaoEur.getText() == null ? "" : txtTaxaConversaoEur.getText().trim();

        if (nome.isEmpty() || simbolo.isEmpty() || taxaStr.isEmpty()) {
            lblErro.setText("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        Double taxaConversaoEur;
        try {
            taxaConversaoEur = Double.parseDouble(taxaStr.replace(",", "."));
            if (taxaConversaoEur <= 0) {
                lblErro.setText("A taxa de conversão deve ser maior que zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblErro.setText("A taxa de conversão deve ser um número válido.");
            return;
        }

        var request = new UpdateMoedaRequest(
                nome,
                simbolo,
                taxaConversaoEur
        );

        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.update(moeda.id.toString(), request, state -> {
            if (state.isLoading()) {
                btnGuardar.setText("A guardar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Moeda \"" + request.nome + "\" atualizada com sucesso.").replaceAll("\\R", " ").strip());
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
