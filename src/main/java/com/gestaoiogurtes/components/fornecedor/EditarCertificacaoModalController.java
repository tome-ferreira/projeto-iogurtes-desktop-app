package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.models.fornecedor.UpdateCertificacaoRequest;
import com.gestaoiogurtes.services.FornecedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditarCertificacaoModalController {

    @FXML
    private DatePicker dpDataInicio;
    @FXML
    private DatePicker dpDataFim;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnGuardar;

    private Stage dialogStage;
    private FornecedorService service;
    private String fornecedorCertificacaoId;
    private Runnable onSuccess;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void show(String fornecedorCertificacaoId,
            String dataInicio, String dataFim,
            FornecedorService service,
            Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditarCertificacaoModalController.class
                            .getResource("/fxml/components/fornecedor/EditarCertificacaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Certificação");
            stage.setScene(new Scene(root));

            EditarCertificacaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.fornecedorCertificacaoId = fornecedorCertificacaoId;
            ctrl.onSuccess = onSuccess;
            ctrl.preencherDatas(dataInicio, dataFim);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lblErro.setText("");
    }

    private void preencherDatas(String dataInicio, String dataFim) {
        if (dataInicio != null && !dataInicio.isBlank()) {
            try {
                dpDataInicio.setValue(LocalDate.parse(dataInicio, FMT));
            } catch (Exception ignored) {
            }
        }
        if (dataFim != null && !dataFim.isBlank()) {
            try {
                dpDataFim.setValue(LocalDate.parse(dataFim, FMT));
            } catch (Exception ignored) {
            }
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");

        LocalDate inicio = dpDataInicio.getValue();
        LocalDate fim = dpDataFim.getValue();

        if (inicio == null || fim == null) {
            lblErro.setText("Preencha ambas as datas.");
            return;
        }
        if (!fim.isAfter(inicio)) {
            lblErro.setText("A data de fim deve ser posterior à data de início.");
            return;
        }

        btnGuardar.setDisable(true);
        btnGuardar.setText("A guardar...");

        var req = new UpdateCertificacaoRequest(inicio.format(FMT), fim.format(FMT));
        service.updateCertificacao(fornecedorCertificacaoId, req, state -> {
            if (state.isLoading()) {
                // já desactivado acima
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.run();
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnGuardar.setText("Guardar");
                lblErro.setText(
                        "Erro: " + (state.getErrorMessage() != null ? state.getErrorMessage() : "desconhecido"));
            }
        });
    }
}
