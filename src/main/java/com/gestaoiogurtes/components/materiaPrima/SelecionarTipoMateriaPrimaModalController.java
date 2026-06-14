package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.TipoMateriaPrimaSelecao;
import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import com.gestaoiogurtes.services.TipoMateriaPrimaService;
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

public class SelecionarTipoMateriaPrimaModalController {

    @FXML private Button           btnAnterior;
    @FXML private Button           btnProximo;
    @FXML private Label            lblPagina;
    @FXML private VBox             listaContainer;
    @FXML private HBox             hboxLoading;
    @FXML private Button           btnContinuar;

    private Stage                    dialogStage;
    private TipoMateriaPrimaService  tipoService;
    private Consumer<TipoMateriaPrimaSelecao> onConfirm;

    private String selectedId;
    private String selectedNome;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    public static void show(
            TipoMateriaPrimaService tipoService,
            Window owner,
            String preSelectedId,
            Consumer<TipoMateriaPrimaSelecao> onConfirm) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarTipoMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/SelecionarTipoMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Selecione o Tipo de Matéria Prima");
            stage.setScene(new Scene(root));

            SelecionarTipoMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage = stage;
            ctrl.tipoService = tipoService;
            ctrl.onConfirm   = onConfirm;

            if (preSelectedId != null && !preSelectedId.isBlank()) {
                ctrl.selectedId = preSelectedId;
            }

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
    private void handleContinuar() {
        if (selectedId != null && !selectedId.isBlank()) {
            onConfirm.accept(new TipoMateriaPrimaSelecao(selectedId, selectedNome));
        }
        dialogStage.close();
    }

    private void carregarPagina(int page) {
        setLoadingVisible(true);
        listaContainer.getChildren().clear();

        tipoService.getAll(page, PAGE_SIZE, state -> {
            if (state.isLoading()) {
                setLoadingVisible(true);
            } else if (state.isSuccess()) {
                setLoadingVisible(false);
                var resposta = state.getData();
                if (resposta != null) {
                    currentPage = page;
                    totalPages  = Math.max(1, resposta.totalPages);

                    lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    btnAnterior.setDisable(resposta.first);
                    btnProximo.setDisable(resposta.last);

                    renderizarLista(resposta.content != null ? resposta.content : List.of());
                }
            } else if (state.isError()) {
                setLoadingVisible(false);
                Label lblErro = new Label("Erro ao carregar tipos: " + state.getErrorMessage());
                lblErro.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(lblErro);
            }
        });
    }

    private void renderizarLista(List<TipoMateriaPrimaResponse> tipos) {
        listaContainer.getChildren().clear();

        if (tipos.isEmpty()) {
            Label lblVazio = new Label("Sem tipos disponíveis.");
            lblVazio.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(lblVazio);
            return;
        }

        for (TipoMateriaPrimaResponse tipo : tipos) {
            String id   = tipo.id   != null ? tipo.id.toString() : "";
            String nome = tipo.nome != null ? tipo.nome : "(sem nome)";

            HBox linha = new HBox();
            linha.getStyleClass().add("selecionar-tipo-row");

            RadioButton radio = new RadioButton();
            radio.getStyleClass().add("selecionar-tipo-radio");
            radio.setSelected(id.equals(selectedId));

            Label lblNome = new Label(nome);
            lblNome.getStyleClass().add("selecionar-tipo-nome");

            linha.getChildren().addAll(radio, lblNome);

            Runnable seleccionar = () -> {
                selectedId   = id;
                selectedNome = nome;
                btnContinuar.setDisable(false);
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

            radio.setOnAction(e -> seleccionar.run());
            linha.setOnMouseClicked(e -> seleccionar.run());

            listaContainer.getChildren().add(linha);
        }

        if (selectedId != null && !selectedId.isBlank()) {
            btnContinuar.setDisable(false);
        }
    }

    private void setLoadingVisible(boolean visible) {
        hboxLoading.setVisible(visible);
        hboxLoading.setManaged(visible);
    }
}
