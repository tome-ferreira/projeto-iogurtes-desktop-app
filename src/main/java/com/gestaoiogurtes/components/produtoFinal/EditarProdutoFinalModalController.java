package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.models.produtoFinal.UpdateProdutoFinalRequest;
import com.gestaoiogurtes.services.ProdutoFinalService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class EditarProdutoFinalModalController {

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtDescricao;
    @FXML
    private TextField txtAbreviacaoSabor;
    @FXML
    private ComboBox<String> cbEstadoFisico;
    @FXML
    private TextField txtValidadeDias;
    @FXML
    private TextField txtPrecoVenda;
    @FXML
    private TextField txtPrecoPorKg;
    @FXML
    private TextField txtTaxaIva;
    @FXML
    private TextField txtQuantidadeLote;
    @FXML
    private CheckBox chkVisivelCliente;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnGuardar;

    private Stage dialogStage;
    private ProdutoFinalService service;
    private Consumer<String> onSuccess;
    private String produtoId;

    public static void show(
            ProdutoFinalResponse produto,
            ProdutoFinalService service,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditarProdutoFinalModalController.class
                            .getResource("/fxml/components/produtoFinal/EditarProdutoFinalModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Editar Produto Final");
            stage.setScene(new Scene(root));

            EditarProdutoFinalModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.onSuccess = onSuccess;
            ctrl.setProduto(produto);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cbEstadoFisico.getItems().addAll(EnumDisplayHelper.estadoFisicoLabels());
        lblErro.setText("");
    }

    private void setProduto(ProdutoFinalResponse p) {
        this.produtoId = p.id != null ? p.id.toString() : null;

        txtNome.setText(p.nome != null ? p.nome : "");
        txtDescricao.setText(p.descricao != null ? p.descricao : "");
        txtAbreviacaoSabor.setText(p.abreviacaoSabor != null ? p.abreviacaoSabor : "");

        if (p.estadoFisico != null) {
            String label = EnumDisplayHelper.estadoFisico(p.estadoFisico);
            cbEstadoFisico.setValue(label);
        }

        txtValidadeDias.setText(p.validadeDias != null ? String.valueOf(p.validadeDias) : "");
        txtPrecoVenda.setText(p.precoVenda != null ? String.valueOf(p.precoVenda) : "");
        txtPrecoPorKg.setText(p.precoPorKg != null ? String.valueOf(p.precoPorKg) : "");
        txtTaxaIva.setText(p.taxaIva != null ? String.valueOf(p.taxaIva) : "");
        txtQuantidadeLote.setText(p.quantidadeLote != null ? String.valueOf(p.quantidadeLote) : "");
        chkVisivelCliente.setSelected(Boolean.TRUE.equals(p.visivelCliente));
    }

    @FXML
    private void handleGuardar() {
        lblErro.setText("");

        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String abrev = txtAbreviacaoSabor.getText() != null ? txtAbreviacaoSabor.getText().trim() : "";
        String estadoLabel = cbEstadoFisico.getValue();
        String taxaIvaStr = txtTaxaIva.getText() != null ? txtTaxaIva.getText().trim() : "";
        String loteStr = txtQuantidadeLote.getText() != null ? txtQuantidadeLote.getText().trim() : "";

        if (nome.isEmpty()) {
            mostrarErro("O nome é obrigatório.");
            return;
        }
        if (abrev.length() != 3 || !abrev.matches("[A-Za-z]{3}")) {
            mostrarErro("A abreviação do sabor deve ter exactamente 3 letras (A-Z).");
            return;
        }
        if (estadoLabel == null || estadoLabel.isBlank()) {
            mostrarErro("O estado físico é obrigatório.");
            return;
        }
        if (taxaIvaStr.isEmpty()) {
            mostrarErro("A taxa IVA é obrigatória.");
            return;
        }
        if (loteStr.isEmpty()) {
            mostrarErro("A quantidade lote é obrigatória.");
            return;
        }

        Double taxaIva;
        try {
            taxaIva = Double.parseDouble(taxaIvaStr);
            if (taxaIva < 0) {
                mostrarErro("A taxa IVA não pode ser negativa.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Taxa IVA inválida.");
            return;
        }

        Integer quantidadeLote;
        try {
            quantidadeLote = Integer.parseInt(loteStr);
            if (quantidadeLote < 1) {
                mostrarErro("A quantidade lote deve ser pelo menos 1.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Quantidade lote inválida.");
            return;
        }

        Integer validadeDias = null;
        String validadeStr = txtValidadeDias.getText() != null ? txtValidadeDias.getText().trim() : "";
        if (!validadeStr.isEmpty()) {
            try {
                validadeDias = Integer.parseInt(validadeStr);
                if (validadeDias < 1) {
                    mostrarErro("A validade deve ser pelo menos 1 dia.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Validade inválida.");
                return;
            }
        }

        Double precoVenda = null;
        String precoVendaStr = txtPrecoVenda.getText() != null ? txtPrecoVenda.getText().trim() : "";
        if (!precoVendaStr.isEmpty()) {
            try {
                precoVenda = Double.parseDouble(precoVendaStr);
                if (precoVenda < 0.01) {
                    mostrarErro("O preço de venda deve ser pelo menos 0.01.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Preço de venda inválido.");
                return;
            }
        }

        Double precoPorKg = null;
        String precoPorKgStr = txtPrecoPorKg.getText() != null ? txtPrecoPorKg.getText().trim() : "";
        if (!precoPorKgStr.isEmpty()) {
            try {
                precoPorKg = Double.parseDouble(precoPorKgStr);
                if (precoPorKg < 0.01) {
                    mostrarErro("O preço por kg deve ser pelo menos 0.01.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Preço por kg inválido.");
                return;
            }
        }

        UpdateProdutoFinalRequest request = new UpdateProdutoFinalRequest();
        request.nome = nome;
        request.descricao = txtDescricao.getText() != null ? txtDescricao.getText().trim() : null;
        request.abreviacaoSabor = abrev;
        request.estadoFisico = EnumDisplayHelper.estadoFisicoParaApi(estadoLabel);
        request.validadeDias = validadeDias;
        request.precoVenda = precoVenda;
        request.precoPorKg = precoPorKg;
        request.taxaIva = taxaIva;
        request.visivelCliente = chkVisivelCliente.isSelected();
        request.quantidadeLote = quantidadeLote;

        btnGuardar.setDisable(true);
        btnGuardar.setText("A guardar...");

        service.update(produtoId, request, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Produto Final actualizado com sucesso.");
                }
            } else if (state.isError()) {
                btnGuardar.setDisable(false);
                btnGuardar.setText("Guardar Alterações");
                mostrarErro(state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg != null ? msg : "Ocorreu um erro desconhecido.");
    }
}
