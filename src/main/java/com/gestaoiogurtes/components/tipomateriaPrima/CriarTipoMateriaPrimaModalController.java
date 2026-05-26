package com.gestaoiogurtes.components.tipomateriaPrima;

import com.gestaoiogurtes.models.tipoMateriaPrima.CreateTipoMateriaPrimaRequest;
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
import java.util.function.Consumer;

public class CriarTipoMateriaPrimaModalController {

    private TipoMateriaPrimaService service;

    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtTaxaIva;
    @FXML private Label lblErro;
    @FXML private Button btnCriar;
    @FXML private Button btnCancelar;

    private Stage dialogStage;
    private Consumer<String> onSuccess;

    public static void show(TipoMateriaPrimaService service, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(CriarTipoMateriaPrimaModalController.class.getResource("/fxml/components/tipomateriaPrima/CriarTipoMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Novo Tipo de Matéria Prima");

            Scene scene = new Scene(root);
            stage.setScene(scene);

            CriarTipoMateriaPrimaModalController controller = loader.getController();
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

    private void setService(TipoMateriaPrimaService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        // Validation logic or formatters could go here
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleCriar() {
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

        var request = new CreateTipoMateriaPrimaRequest(
                nome,
                descricao,
                taxaIva
        );

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);
        lblErro.setText("");

        service.create(request, state -> {
            if (state.isLoading()) {
                btnCriar.setText("A criar...");
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.accept(("Tipo de Matéria Prima \"" + request.nome + "\" criado com sucesso.").replaceAll("\\R", " ").strip());
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCancelar.setDisable(false);
                btnCriar.setText("Criar Tipo");
                String erro = state.getErrorMessage() != null ? state.getErrorMessage() : "Erro desconhecido";
                lblErro.setText(("Erro: " + erro).replaceAll("\\R", " ").strip());
            }
        });
    }
}
