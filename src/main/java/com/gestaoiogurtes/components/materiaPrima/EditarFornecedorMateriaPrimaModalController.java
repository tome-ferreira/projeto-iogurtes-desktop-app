package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaFornecedorResponse;
import com.gestaoiogurtes.models.materiaPrima.UpdateFornecedorMateriaPrimaRequest;
import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.MoedaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class EditarFornecedorMateriaPrimaModalController {

    @FXML
    private Label lblFornecedor;
    @FXML
    private ComboBox<MoedaResponse> cbMoeda;
    @FXML
    private TextField txtPrecoUnitario;
    @FXML
    private TextField txtPrazo;
    @FXML
    private CheckBox chkPreferencial;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnGuardar;

    private Stage dialogStage;
    private MateriaPrimaService service;
    private MoedaService moedaService;
    private Runnable onSuccess;
    private String fornecedorLinkId;

    public static void show(MateriaPrimaFornecedorResponse item, MateriaPrimaService service,
            Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditarFornecedorMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/EditarFornecedorMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Fornecedor");
            stage.setScene(new Scene(root));

            EditarFornecedorMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.moedaService = new MoedaService();
            ctrl.onSuccess = onSuccess;
            ctrl.fornecedorLinkId = item.id != null ? item.id.toString() : null;

            ctrl.carregarMoedas(item);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lblErro.setText("");

        cbMoeda.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MoedaResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.codigo + " – " + item.simbolo);
            }
        });
        cbMoeda.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MoedaResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.codigo + " – " + item.simbolo);
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleGuardar() {
        mostrarErro("");

        if (cbMoeda.getValue() == null) {
            mostrarErro("Selecione uma moeda.");
            return;
        }
        String precoStr = txtPrecoUnitario.getText().trim();
        if (precoStr.isEmpty()) {
            mostrarErro("Introduza o preço unitário.");
            return;
        }
        double preco;
        try {
            preco = Double.parseDouble(precoStr.replace(',', '.'));
            if (preco < 0.01)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarErro("Preço unitário inválido (mínimo 0.01).");
            return;
        }

        Integer prazo = null;
        String prazoStr = txtPrazo.getText().trim();
        if (!prazoStr.isEmpty()) {
            try {
                prazo = Integer.parseInt(prazoStr);
                if (prazo < 1)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarErro("Prazo de entrega inválido (mínimo 1 dia).");
                return;
            }
        }

        var req = new UpdateFornecedorMateriaPrimaRequest();
        req.moedaId = cbMoeda.getValue().id;
        req.precoUnitario = preco;
        req.prazoEstimadoEntregaDias = prazo;
        req.preferencial = chkPreferencial.isSelected();

        btnGuardar.setDisable(true);
        btnGuardar.setText("A guardar...");

        service.updateFornecedor(fornecedorLinkId, req, state -> {
            if (state.isLoading()) {
                // já desativado acima
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.run();
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnGuardar.setText("Guardar");
                mostrarErro("Erro: " + state.getErrorMessage());
            }
        });
    }

    private void carregarMoedas(MateriaPrimaFornecedorResponse item) {
        moedaService.getAll(0, 100, state -> {
            if (state.isSuccess() && state.getData() != null) {
                List<MoedaResponse> moedas = state.getData().content != null
                        ? state.getData().content
                        : List.of();
                cbMoeda.getItems().setAll(moedas);

                // Pré-preencher campos
                lblFornecedor.setText(item.fornecedorNome != null ? item.fornecedorNome : "—");
                if (item.precoUnitario != null)
                    txtPrecoUnitario.setText(String.format("%.2f", item.precoUnitario));
                if (item.prazoEstimadoEntregaDias != null)
                    txtPrazo.setText(String.valueOf(item.prazoEstimadoEntregaDias));
                chkPreferencial.setSelected(Boolean.TRUE.equals(item.preferencial));

                // Pré-selecionar moeda por id
                if (item.moedaId != null) {
                    moedas.stream()
                            .filter(m -> m.id != null && m.id.equals(item.moedaId))
                            .findFirst()
                            .ifPresent(cbMoeda::setValue);
                }
            }
        });
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
    }
}
