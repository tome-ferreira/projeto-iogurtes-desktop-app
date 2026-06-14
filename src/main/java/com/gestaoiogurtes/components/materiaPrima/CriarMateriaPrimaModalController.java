package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.CreateMateriaPrimaRequest;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.TipoMateriaPrimaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class CriarMateriaPrimaModalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtUnidade;
    @FXML private TextField txtStockMinimo;
    @FXML private Label     lblTipoNome;
    @FXML private Button    btnSelecionarTipo;
    @FXML private Label     lblErro;
    @FXML private Button    btnGuardar;

    public Stage dialogStage;
    public MateriaPrimaService service;
    public TipoMateriaPrimaService tipoService;
    public Consumer<String> onSuccess;

    private String selectedTipoId;

    public static void show(MateriaPrimaService service, TipoMateriaPrimaService tipoService, Window owner, Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CriarMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/CriarMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Criar Matéria Prima");
            stage.setScene(new Scene(root));

            CriarMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.tipoService = tipoService;
            ctrl.onSuccess = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelecionarTipo() {
        SelecionarTipoMateriaPrimaModalController.show(
                tipoService,
                dialogStage,
                null,
                selecao -> {
                    selectedTipoId = selecao.id;
                    lblTipoNome.setText(selecao.nome);
                    lblTipoNome.setVisible(true);
                    lblTipoNome.setManaged(true);
                    btnSelecionarTipo.setText("Alterar Tipo");
                }
        );
    }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");

        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String unidade = txtUnidade.getText() != null ? txtUnidade.getText().trim() : "";
        String stockMinimoStr = txtStockMinimo.getText() != null ? txtStockMinimo.getText().trim() : "";

        if (nome.isEmpty() || stockMinimoStr.isEmpty() || selectedTipoId == null || selectedTipoId.isBlank()) {
            mostrarErro("Por favor, preencha todos os campos obrigatórios (*).");
            return;
        }

        double stockMinimo;
        try {
            stockMinimo = Double.parseDouble(stockMinimoStr);
            if (stockMinimo < 0) {
                mostrarErro("O stock mínimo não pode ser negativo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarErro("O stock mínimo deve ser um número válido.");
            return;
        }

        CreateMateriaPrimaRequest request = new CreateMateriaPrimaRequest();
        request.nome = nome;
        request.unidade = unidade;
        request.stockMinimo = stockMinimo;
        request.tipoId = UUID.fromString(selectedTipoId);

        btnGuardar.setDisable(true);
        btnGuardar.setText("A guardar...");

        service.create(request, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Matéria Prima criada com sucesso.");
                }
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnGuardar.setText("Guardar");
                mostrarErro("Erro: " + state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
    }
}
