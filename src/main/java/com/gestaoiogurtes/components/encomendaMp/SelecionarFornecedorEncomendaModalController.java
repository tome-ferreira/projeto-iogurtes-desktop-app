package com.gestaoiogurtes.components.encomendaMp;

import com.gestaoiogurtes.models.encomendaMp.FornecedorEncomendaSelecao;
import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
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

/**
 * Modal para seleccionar um fornecedor ao criar uma encomenda de matéria prima.
 * Baseado em {@code SelecionarEmpresaModalController} — mesma estrutura e comportamento.
 * <ul>
 *   <li>Carrega 10 fornecedores por página via {@link FornecedorService#getAll}.</li>
 *   <li>A selecção persiste entre páginas.</li>
 *   <li>O botão "Continuar" só fica activo quando um fornecedor está seleccionado.</li>
 *   <li>{@code onConfirm} é chamado <strong>apenas</strong> ao clicar "Continuar".</li>
 * </ul>
 */
public class SelecionarFornecedorEncomendaModalController {

    @FXML private Button btnAnterior;
    @FXML private Button btnProximo;
    @FXML private Label  lblPagina;
    @FXML private VBox   listaContainer;
    @FXML private HBox   hboxLoading;
    @FXML private Button btnContinuar;

    private Stage                               dialogStage;
    private FornecedorService                   fornecedorService;
    private Consumer<FornecedorEncomendaSelecao> onConfirm;

    private String selectedId;
    private String selectedNome;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    /**
     * Abre o modal de selecção de fornecedor para encomenda MP.
     *
     * @param fornecedorService serviço para obter fornecedores
     * @param owner             janela proprietária
     * @param onConfirm         callback invocado ao clicar "Continuar"; nunca chamado ao fechar com X
     */
    public static void show(
            FornecedorService fornecedorService,
            Window owner,
            Consumer<FornecedorEncomendaSelecao> onConfirm) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SelecionarFornecedorEncomendaModalController.class
                            .getResource("/fxml/components/encomendaMp/SelecionarFornecedorEncomendaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle("Selecione um Fornecedor");
            stage.setScene(new Scene(root));

            SelecionarFornecedorEncomendaModalController ctrl = loader.getController();
            ctrl.dialogStage      = stage;
            ctrl.fornecedorService = fornecedorService;
            ctrl.onConfirm        = onConfirm;

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
            onConfirm.accept(new FornecedorEncomendaSelecao(selectedId, selectedNome));
        }
        dialogStage.close();
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
                    totalPages  = Math.max(1, resposta.totalPages);

                    lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    btnAnterior.setDisable(resposta.first);
                    btnProximo.setDisable(resposta.last);

                    renderizarLista(resposta.content != null ? resposta.content : List.of());
                }
            } else if (state.isError()) {
                setLoadingVisible(false);
                Label lblErro = new Label("Erro ao carregar fornecedores: " + state.getErrorMessage());
                lblErro.getStyleClass().add("selecionar-tipo-erro");
                listaContainer.getChildren().add(lblErro);
            }
        });
    }

    private void renderizarLista(List<FornecedorResponse> fornecedores) {
        listaContainer.getChildren().clear();

        if (fornecedores.isEmpty()) {
            Label lblVazio = new Label("Sem fornecedores disponíveis.");
            lblVazio.getStyleClass().add("selecionar-tipo-vazio");
            listaContainer.getChildren().add(lblVazio);
            return;
        }

        for (FornecedorResponse f : fornecedores) {
            String id   = f.id   != null ? f.id.toString()   : "";
            String nome = f.nome != null ? f.nome              : "(sem nome)";

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
