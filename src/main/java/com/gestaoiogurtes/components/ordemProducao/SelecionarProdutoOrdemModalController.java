package com.gestaoiogurtes.components.ordemProducao;

import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.models.ordemProducao.ProdutoOrdemSelecao;
import com.gestaoiogurtes.services.OrdemProducaoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class SelecionarProdutoOrdemModalController {

    @FXML
    private VBox listaContainer;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnProxima;
    @FXML
    private Label lblPagina;
    @FXML
    private TextField txtQuantidade;
    @FXML
    private Label lblErroQuantidade;
    @FXML
    private Label lblErroGlobal;
    @FXML
    private Button btnAdicionar;

    private Stage dialogStage;
    private OrdemProducaoService service;
    private Consumer<ProdutoOrdemSelecao> onConfirm;

    private int currentPage = 0;
    private final int pageSize = 10;
    private int totalPages = 0;
    private List<ProdutoFinalResponse> produtosPage = List.of();

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private String selectedId = null;
    private String selectedNome = null;

    public static void show(
            OrdemProducaoService service,
            Window owner,
            Consumer<ProdutoOrdemSelecao> onConfirm) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarProdutoOrdemModalController.class
                            .getResource("/fxml/components/ordemProducao/SelecionarProdutoOrdemModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Adicionar Produto");
            stage.setScene(new Scene(root));

            SelecionarProdutoOrdemModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.service = service;
            ctrl.onConfirm = onConfirm;

            ctrl.carregarPagina();

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lblErroGlobal.setText("");
        lblErroQuantidade.setText("");
        lblErroQuantidade.setVisible(false);
        lblErroQuantidade.setManaged(false);

        // Listener da quantidade para validação e ativar o botão "Adicionar"
        txtQuantidade.textProperty().addListener((obs, old, val) -> validarAdicionar());
        toggleGroup.selectedToggleProperty().addListener((obs, old, val) -> validarAdicionar());
    }

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarPagina();
        }
    }

    @FXML
    private void handleProxima() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarPagina();
        }
    }

    private void carregarPagina() {
        listaContainer.getChildren().clear();
        Label loading = new Label("A carregar produtos...");
        loading.getStyleClass().add("selecionar-tipo-vazio");
        listaContainer.getChildren().add(loading);

        service.getAllProdutos(currentPage, pageSize, state -> {
            if (state.isSuccess() && state.getData() != null) {
                produtosPage = state.getData().content != null ? state.getData().content : List.of();
                totalPages = state.getData().totalPages;

                lblPagina.setText("Página " + (currentPage + 1) + " de " + Math.max(1, totalPages));
                btnAnterior.setDisable(state.getData().first);
                btnProxima.setDisable(state.getData().last);

                renderizarLista();
            } else if (state.isError()) {
                listaContainer.getChildren().clear();
                Label err = new Label("Erro ao carregar: " + state.getErrorMessage());
                err.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(err);
            }
        });
    }

    private void renderizarLista() {
        listaContainer.getChildren().clear();

        if (produtosPage.isEmpty()) {
            Label vazia = new Label("Nenhum produto encontrado.");
            vazia.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(vazia);
            return;
        }

        for (ProdutoFinalResponse prod : produtosPage) {
            HBox row = new HBox();
            row.getStyleClass().add("selecionar-tipo-row");
            row.setAlignment(Pos.CENTER_LEFT);

            RadioButton rb = new RadioButton();
            rb.setToggleGroup(toggleGroup);
            rb.getStyleClass().add("selecionar-tipo-radio");

            if (prod.id.toString().equals(selectedId)) {
                rb.setSelected(true);
            }

            VBox boxNomes = new VBox(2);
            HBox.setHgrow(boxNomes, Priority.ALWAYS);

            Label lblNome = new Label(prod.nome);
            lblNome.getStyleClass().add("selecionar-tipo-nome");

            Label lblSku = new Label("SKU: " + (prod.codigoSku != null ? prod.codigoSku : "—"));
            lblSku.getStyleClass().add("selecionar-materia-preco");

            boxNomes.getChildren().addAll(lblNome, lblSku);

            row.getChildren().addAll(rb, boxNomes);

            // Clique na linha também seleciona o radio
            row.setOnMouseClicked(e -> {
                rb.setSelected(true);
                selectedId = prod.id.toString();
                selectedNome = prod.nome;
                validarAdicionar();
            });

            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectedId = prod.id.toString();
                    selectedNome = prod.nome;
                }
            });

            listaContainer.getChildren().add(row);
        }
    }

    private void validarAdicionar() {
        boolean temProduto = selectedId != null && !selectedId.isBlank();
        boolean qtdValida = parseQuantidade() != null;

        btnAdicionar.setDisable(!temProduto || !qtdValida);
    }

    private Double parseQuantidade() {
        String texto = txtQuantidade.getText();
        if (texto == null || texto.isBlank())
            return null;
        try {
            double v = Double.parseDouble(texto.replace(",", "."));
            if (v > 0)
                return v;
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @FXML
    private void handleAdicionar() {
        Double qtd = parseQuantidade();
        if (selectedId == null || selectedNome == null) {
            lblErroGlobal.setText("Selecione um produto.");
            return;
        }
        if (qtd == null) {
            lblErroQuantidade.setText("Insira uma quantidade válida (> 0).");
            lblErroQuantidade.setVisible(true);
            lblErroQuantidade.setManaged(true);
            return;
        }

        dialogStage.close();
        if (onConfirm != null) {
            onConfirm.accept(new ProdutoOrdemSelecao(selectedId, selectedNome, qtd));
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }
}
