package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.AddFornecedorMateriaPrimaRequest;
import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.services.FornecedorService;
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

public class AdicionarFornecedorMateriaPrimaModalController {

    @FXML
    private Label lblFornecedorNome;
    @FXML
    private Button btnSelecionarFornecedor;
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
    private Button btnAdicionar;

    private Stage dialogStage;
    private MateriaPrimaService service;
    private FornecedorService fornecedorService;
    private MoedaService moedaService;
    private String materiaId;
    private Runnable onSuccess;

    private String selectedFornecedorId;

    public static void show(String materiaId, MateriaPrimaService service,
            Window owner, Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AdicionarFornecedorMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/AdicionarFornecedorMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Adicionar Fornecedor");
            stage.setScene(new Scene(root));

            AdicionarFornecedorMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.fornecedorService = new FornecedorService();
            ctrl.moedaService = new MoedaService();
            ctrl.materiaId = materiaId;
            ctrl.onSuccess = onSuccess;

            ctrl.carregarMoedas();

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lblErro.setText("");

        // Renderizar moeda como "codigo – simbolo"
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
    private void handleSelecionarFornecedor() {
        SelecionarFornecedorParaMateriaModalController.show(
                fornecedorService, dialogStage, selectedFornecedorId,
                selecao -> {
                    selectedFornecedorId = selecao.id;
                    lblFornecedorNome.setText(selecao.nome);
                    btnSelecionarFornecedor.setText("Alterar Fornecedor");
                });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleAdicionar() {
        mostrarErro("");

        // Validação
        if (selectedFornecedorId == null || selectedFornecedorId.isBlank()) {
            mostrarErro("Selecione um fornecedor.");
            return;
        }
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

        var req = new AddFornecedorMateriaPrimaRequest();
        req.fornecedorId = java.util.UUID.fromString(selectedFornecedorId);
        req.moedaId = cbMoeda.getValue().id;
        req.precoUnitario = preco;
        req.prazoEstimadoEntregaDias = prazo;
        req.preferencial = chkPreferencial.isSelected();

        btnAdicionar.setDisable(true);
        btnAdicionar.setText("A adicionar...");

        service.addFornecedor(materiaId, req, state -> {
            if (state.isLoading()) {
                // já desactivado acima
            } else if (state.isSuccess()) {
                dialogStage.close();
                onSuccess.run();
            } else if (state.isError()) {
                btnAdicionar.setDisable(false);
                btnAdicionar.setText("Adicionar");
                mostrarErro("Erro: " + state.getErrorMessage());
            }
        });
    }

    private void carregarMoedas() {
        moedaService.getAll(0, 100, state -> {
            if (state.isSuccess() && state.getData() != null) {
                List<MoedaResponse> moedas = state.getData().content != null
                        ? state.getData().content
                        : List.of();
                cbMoeda.getItems().setAll(moedas);
            }
        });
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
    }
}
