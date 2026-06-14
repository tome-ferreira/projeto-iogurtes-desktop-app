package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import com.gestaoiogurtes.models.materiaPrima.FornecedorMateriaPrimaSelecao;
import com.gestaoiogurtes.services.FornecedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class SelecionarFornecedorParaMateriaModalController {

    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnProximo;
    @FXML
    private Label lblPagina;
    @FXML
    private VBox listaContainer;
    @FXML
    private HBox hboxLoading;
    @FXML
    private Button btnContinuar;

    private Stage dialogStage;
    private FornecedorService fornecedorService;
    private Consumer<FornecedorMateriaPrimaSelecao> onConfirm;
    private String preSelectedId;

    private String selectedId;
    private String selectedNome;

    private int currentPage = 0;
    private int totalPages = 1;
    private static final int PAGE_SIZE = 10;

    public static void show(FornecedorService fornecedorService, Window owner,
            String preSelectedId,
            Consumer<FornecedorMateriaPrimaSelecao> onConfirm) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarFornecedorParaMateriaModalController.class
                            .getResource("/fxml/components/materiaPrima/SelecionarFornecedorParaMateriaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Selecionar Fornecedor");
            stage.setScene(new Scene(root));

            SelecionarFornecedorParaMateriaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.fornecedorService = fornecedorService;
            ctrl.onConfirm = onConfirm;
            ctrl.preSelectedId = preSelectedId;
            ctrl.selectedId = preSelectedId;

            ctrl.carregarPagina(0);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        btnContinuar.setDisable(true);
    }

    @FXML
    private void handleAnterior() {
        if (currentPage > 0)
            carregarPagina(currentPage - 1);
    }

    @FXML
    private void handleProximo() {
        if (currentPage < totalPages - 1)
            carregarPagina(currentPage + 1);
    }

    @FXML
    private void handleContinuar() {
        if (selectedId != null && !selectedId.isBlank()) {
            onConfirm.accept(new FornecedorMateriaPrimaSelecao(selectedId, selectedNome));
            dialogStage.close();
        }
    }

    private void carregarPagina(int page) {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        fornecedorService.getAll(page, PAGE_SIZE, state -> {
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
                var lbl = new Label("Erro ao carregar: " + state.getErrorMessage());
                lbl.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(lbl);
            }
        });
    }

    private void renderizarLista(List<FornecedorResponse> fornecedores) {
        listaContainer.getChildren().clear();

        if (fornecedores.isEmpty()) {
            var lbl = new Label("Sem fornecedores disponíveis.");
            lbl.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(lbl);
            return;
        }

        for (FornecedorResponse f : fornecedores) {
            String id = f.id != null ? f.id.toString() : "";
            String nome = f.nome != null ? f.nome : "(sem nome)";

            var linha = new HBox();
            linha.getStyleClass().add("selecionar-tipo-row");

            var radio = new RadioButton();
            radio.getStyleClass().add("selecionar-tipo-radio");
            radio.setSelected(id.equals(selectedId));

            var lblNome = new Label(nome);
            lblNome.getStyleClass().add("selecionar-tipo-nome");

            linha.getChildren().addAll(radio, lblNome);

            Runnable selecionar = () -> {
                selectedId = id;
                selectedNome = nome;
                btnContinuar.setDisable(false);
                // Desmarcar todos os outros radio buttons
                listaContainer.getChildren().forEach(node -> {
                    if (node instanceof HBox hb) {
                        hb.getChildren().stream()
                                .filter(c -> c instanceof RadioButton)
                                .map(c -> (RadioButton) c)
                                .forEach(rb -> rb.setSelected(false));
                    }
                });
                radio.setSelected(true);
            };

            radio.setOnAction(e -> selecionar.run());
            linha.setOnMouseClicked(e -> selecionar.run());

            listaContainer.getChildren().add(linha);
        }

        // Restaurar seleção persistente entre páginas
        if (selectedId != null && !selectedId.isBlank()) {
            btnContinuar.setDisable(false);
        }
    }

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
