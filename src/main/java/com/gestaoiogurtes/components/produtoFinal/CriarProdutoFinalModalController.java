package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.models.produtoFinal.ComposicaoItem;
import com.gestaoiogurtes.models.produtoFinal.CreateProdutoFinalRequest;
import com.gestaoiogurtes.models.produtoFinal.MateriaPrimaComposicaoSelecao;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.ProdutoFinalService;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CriarProdutoFinalModalController {

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
    private VBox composicaoContainer;
    @FXML
    private Label lblComposicaoAviso;
    @FXML
    private Label lblErro;
    @FXML
    private Button btnCriar;

    private Stage dialogStage;
    private ProdutoFinalService service;
    private MateriaPrimaService materiaPrimaService;
    private Consumer<String> onSuccess;

    /** ObservableList de itens de composição adicionados pelo utilizador */
    private final ObservableList<MateriaPrimaComposicaoSelecao> composicaoItems = FXCollections.observableArrayList();

    public static void show(
            ProdutoFinalService service,
            MateriaPrimaService materiaPrimaService,
            Window owner,
            Consumer<String> onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CriarProdutoFinalModalController.class
                            .getResource("/fxml/components/produtoFinal/CriarProdutoFinalModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Criar Produto Final");
            stage.setScene(new Scene(root));

            CriarProdutoFinalModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.materiaPrimaService = materiaPrimaService;
            ctrl.onSuccess = onSuccess;

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cbEstadoFisico.getItems().addAll(EnumDisplayHelper.estadoFisicoLabels());
        lblErro.setText("");
        lblComposicaoAviso.setVisible(false);
        lblComposicaoAviso.setManaged(false);
        renderizarComposicao();
    }

    @FXML
    private void handleAdicionarMateria() {
        SelecionarMateriaPrimaComposicaoModalController.show(
                materiaPrimaService,
                dialogStage,
                selecao -> {
                    // Verificar duplicado
                    boolean duplicado = composicaoItems.stream()
                            .anyMatch(item -> item.id.equals(selecao.id));
                    if (duplicado) {
                        lblComposicaoAviso.setText(
                                "A matéria prima \"" + selecao.nome + "\" já foi adicionada à composição.");
                        lblComposicaoAviso.setVisible(true);
                        lblComposicaoAviso.setManaged(true);
                        return;
                    }
                    lblComposicaoAviso.setVisible(false);
                    lblComposicaoAviso.setManaged(false);
                    composicaoItems.add(selecao);
                    renderizarComposicao();
                });
    }

    @FXML
    private void handleCriar() {
        lblErro.setText("");

        // Validação dos campos obrigatórios
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

        // Campos opcionais
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

        List<ComposicaoItem> composicao = new ArrayList<>();
        for (MateriaPrimaComposicaoSelecao sel : composicaoItems) {
            ComposicaoItem item = new ComposicaoItem(
                    UUID.fromString(sel.id),
                    sel.quantidade);
            composicao.add(item);
        }

        CreateProdutoFinalRequest request = new CreateProdutoFinalRequest();
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
        request.composicao = composicao;

        btnCriar.setDisable(true);
        btnCriar.setText("A criar...");

        service.create(request, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.accept("Produto Final criado com sucesso.");
                }
            } else if (state.isError()) {
                btnCriar.setDisable(false);
                btnCriar.setText("Criar Produto Final");
                mostrarErro(state.getErrorMessage());
            }
        });
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private void renderizarComposicao() {
        composicaoContainer.getChildren().clear();

        if (composicaoItems.isEmpty()) {
            Label lblVazio = new Label("Nenhuma matéria prima adicionada.");
            lblVazio.getStyleClass().add("composicao-vazia-label");
            composicaoContainer.getChildren().add(lblVazio);
            return;
        }

        for (int i = 0; i < composicaoItems.size(); i++) {
            MateriaPrimaComposicaoSelecao item = composicaoItems.get(i);
            final int idx = i;

            HBox row = new HBox();
            row.getStyleClass().add("composicao-row");
            row.setAlignment(Pos.CENTER_LEFT);

            Label lblNome = new Label(item.nome);
            lblNome.getStyleClass().add("composicao-row-nome");
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            Label lblQtd = new Label(String.format("%.3f", item.quantidade));
            lblQtd.getStyleClass().add("composicao-row-qtd");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.NEVER);

            Button btnRemover = new Button("Remover");
            btnRemover.getStyleClass().add("btn-remover-materia");
            btnRemover.setOnAction(e -> {
                composicaoItems.remove(idx);
                renderizarComposicao();
            });

            row.getChildren().addAll(lblNome, lblQtd, spacer, btnRemover);
            composicaoContainer.getChildren().add(row);
        }
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg != null ? msg : "Ocorreu um erro desconhecido.");
    }
}
