package com.gestaoiogurtes.components.produtoFinal;

import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
import com.gestaoiogurtes.models.produtoFinal.AddMateriasComposicaoRequest;
import com.gestaoiogurtes.models.produtoFinal.CreateProdutoMateriaRequest;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.ProdutoFinalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
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
import java.util.List;
import java.util.UUID;

public class AdicionarComposicaoModalController {

    @FXML private Button    btnAnterior;
    @FXML private Button    btnProximo;
    @FXML private Label     lblPagina;
    @FXML private VBox      listaContainer;
    @FXML private HBox      hboxLoading;
    @FXML private Button    btnCancelar;
    @FXML private Button    btnAdicionar;
    @FXML private TextField txtQuantidade;
    @FXML private Label     lblQuantidadeErro;

    private Stage dialogStage;
    private MateriaPrimaService materiaPrimaService;
    private ProdutoFinalService produtoFinalService;
    private Runnable onSuccess;

    private String produtoId;
    private String selectedId;
    private String selectedNome;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    public static void show(
            String produtoId,
            ProdutoFinalService produtoFinalService,
            MateriaPrimaService materiaPrimaService,
            Window owner,
            Runnable onSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AdicionarComposicaoModalController.class
                            .getResource("/fxml/components/produtoFinal/AdicionarComposicaoModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Adicionar Matéria Prima à Composição");
            stage.setScene(new Scene(root));

            AdicionarComposicaoModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.produtoId = produtoId;
            ctrl.produtoFinalService = produtoFinalService;
            ctrl.materiaPrimaService = materiaPrimaService;
            ctrl.onSuccess = onSuccess;

            ctrl.carregarPagina(0);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        btnAdicionar.setDisable(true);
        lblQuantidadeErro.setText("");

        txtQuantidade.textProperty().addListener((obs, oldVal, newVal) -> validarEstadoBotao());
    }

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) {
            carregarPagina(currentPage - 1);
        }
    }

    @FXML
    private void handleProximo() {
        if (currentPage < totalPages - 1) {
            carregarPagina(currentPage + 1);
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleAdicionar() {
        lblQuantidadeErro.setText("");

        if (selectedId == null || selectedId.isBlank()) {
            lblQuantidadeErro.setText("Selecione uma matéria prima.");
            return;
        }

        String qtdStr = txtQuantidade.getText() != null ? txtQuantidade.getText().trim() : "";
        if (qtdStr.isEmpty()) {
            lblQuantidadeErro.setText("A quantidade é obrigatória.");
            return;
        }

        double quantidade;
        try {
            quantidade = Double.parseDouble(qtdStr);
            if (quantidade <= 0) {
                lblQuantidadeErro.setText("A quantidade deve ser um número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            lblQuantidadeErro.setText("Quantidade inválida. Introduza um número válido.");
            return;
        }

        btnAdicionar.setDisable(true);
        btnAdicionar.setText("A adicionar...");
        btnCancelar.setDisable(true);

        CreateProdutoMateriaRequest request = new CreateProdutoMateriaRequest(UUID.fromString(selectedId), quantidade);
        AddMateriasComposicaoRequest addRequest = new AddMateriasComposicaoRequest(List.of(request));

        produtoFinalService.addMateriasComposicao(produtoId, addRequest, state -> {
            if (state.isSuccess()) {
                dialogStage.close();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else if (state.isError()) {
                lblQuantidadeErro.setText(state.getErrorMessage());
                btnAdicionar.setDisable(false);
                btnAdicionar.setText("Adicionar");
                btnCancelar.setDisable(false);
            }
        });
    }

    private void carregarPagina(int page) {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        materiaPrimaService.getAll(page, PAGE_SIZE, state -> {
            if (state.isLoading()) {
                setLoadingVisible(true);
            } else if (state.isSuccess()) {
                setLoadingVisible(false);
                var resposta = state.getData();
                if (resposta != null) {
                    currentPage = page;
                    totalPages = Math.max(1, resposta.totalPages);

                    lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    btnAnterior.setDisable(resposta.first);
                    btnProximo.setDisable(resposta.last);

                    renderizarLista(resposta.content != null ? resposta.content : List.of());
                }
            } else if (state.isError()) {
                setLoadingVisible(false);
                Label lblErro = new Label("Erro ao carregar matérias primas: " + state.getErrorMessage());
                lblErro.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(lblErro);
            }
        });
    }

    private void renderizarLista(List<MateriaPrimaResponse> itens) {
        listaContainer.getChildren().clear();

        if (itens.isEmpty()) {
            Label lblVazio = new Label("Sem matérias primas disponíveis.");
            lblVazio.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(lblVazio);
            return;
        }

        for (MateriaPrimaResponse mp : itens) {
            String id   = mp.id   != null ? mp.id.toString()  : "";
            String nome = mp.nome != null ? mp.nome            : "(sem nome)";

            HBox linha = new HBox();
            linha.getStyleClass().add("selecionar-tipo-row");

            RadioButton radio = new RadioButton();
            radio.getStyleClass().add("selecionar-tipo-radio");
            radio.setSelected(id.equals(selectedId));

            Label lblNome = new Label(nome);
            lblNome.getStyleClass().add("selecionar-tipo-nome");
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            linha.getChildren().addAll(radio, lblNome);

            Runnable seleccionar = () -> {
                selectedId = id;
                selectedNome = nome;
                listaContainer.getChildren().forEach(node -> {
                    if (node instanceof HBox hb) {
                        hb.getChildren().stream()
                                .filter(c -> c instanceof RadioButton)
                                .map(c -> (RadioButton) c)
                                .forEach(rb -> rb.setSelected(false));
                    }
                });
                radio.setSelected(true);
                validarEstadoBotao();
            };

            radio.setOnAction(e -> seleccionar.run());
            linha.setOnMouseClicked(e -> seleccionar.run());

            listaContainer.getChildren().add(linha);
        }

        if (selectedId != null && !selectedId.isBlank()) {
            validarEstadoBotao();
        }
    }

    private void validarEstadoBotao() {
        boolean temSeleccao = selectedId != null && !selectedId.isBlank();
        String qtdStr = txtQuantidade.getText() != null ? txtQuantidade.getText().trim() : "";
        boolean quantidadeValida = false;
        if (!qtdStr.isEmpty()) {
            try {
                double v = Double.parseDouble(qtdStr);
                quantidadeValida = v > 0;
            } catch (NumberFormatException ignored) {}
        }
        btnAdicionar.setDisable(!(temSeleccao && quantidadeValida));
    }

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
