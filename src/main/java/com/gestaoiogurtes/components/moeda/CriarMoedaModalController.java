package com.gestaoiogurtes.components.moeda;

import com.gestaoiogurtes.models.moeda.CreateMoedaRequest;
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

public class CriarMoedaModalController {

    private MoedaService service;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtSimbolo;
    @FXML private TextField txtTaxaConversaoEur;
    @FXML private Label lblErro;
    @FXML private Button btnCriar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(MoedaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarMoedaModalController.class.getResource("/fxml/components/moeda/CriarMoedaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Nova Moeda");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CriarMoedaModalController controller = loader.getController();
            controller.setDialogStage(stage);
            controller.setOnSuccess(onSuccess);
            controller.setService(service);

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

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");

        String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim().toUpperCase();
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        String simbolo = txtSimbolo.getText() == null ? "" : txtSimbolo.getText().trim();
        String taxaStr = txtTaxaConversaoEur.getText() == null ? "" : txtTaxaConversaoEur.getText().trim();

        if (codigo.isEmpty() || nome.isEmpty() || simbolo.isEmpty() || taxaStr.isEmpty()) {
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

        var request = new CreateMoedaRequest(
                codigo,
                nome,
                simbolo,
                taxaConversaoEur
        );

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.create(request, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Moeda \"" + request.nome + "\" criada com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Moeda");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
