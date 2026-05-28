package com.gestaoiogurtes.components.fornecedor;

import com.gestaoiogurtes.models.fornecedor.FornecedorCertificacaoResponse;
import com.gestaoiogurtes.services.FornecedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

/**
 * Mini-CRUD de certificações de um fornecedor.
 *
 * <p>Abre-se via {@link #show(String, String, FornecedorService, Window)}.
 * Filtra cliente-side porque a API GET /fornecedores-certificacoes não suporta
 * filtro por fornecedorId. Ver TODO em IFornecedorApiService.
 */
public class CertificacoesFornecedorModalController {

    @FXML private Label lblTitulo;
    @FXML private Label lblPagina;
    @FXML private Button btnAnterior;
    @FXML private Button btnProximo;
    @FXML private VBox   listaContainer;
    @FXML private VBox   loadingOverlay;
    @FXML private Button btnAdicionarCertificacao;

    private Stage            dialogStage;
    private FornecedorService service;
    private String           fornecedorId;
    private String           fornecedorNome;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    // ── Abertura ────────────────────────────────────────────────────────────

    public static void show(String fornecedorId, String fornecedorNome,
                            FornecedorService service, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CertificacoesFornecedorModalController.class
                            .getResource("/fxml/components/fornecedor/CertificacoesFornecedorModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(true);
            stage.setTitle("Certificações – " + fornecedorNome);
            stage.setScene(new Scene(root));

            CertificacoesFornecedorModalController ctrl = loader.getController();
            ctrl.dialogStage    = stage;
            ctrl.service        = service;
            ctrl.fornecedorId   = fornecedorId;
            ctrl.fornecedorNome = fornecedorNome;
            ctrl.lblTitulo.setText("Certificações de " + fornecedorNome);

            ctrl.carregarCertificacoes(0);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Handlers FXML ───────────────────────────────────────────────────────

    @FXML
    private void handleAdicionarCertificacao() {
        SelecionarCertificacaoModalController.show(
                fornecedorId, service, dialogStage,
                () -> carregarCertificacoes(currentPage));
    }

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) carregarCertificacoes(currentPage - 1);
    }

    @FXML
    private void handleProximo() {
        if (currentPage < totalPages - 1) carregarCertificacoes(currentPage + 1);
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }

    // ── Carregamento ────────────────────────────────────────────────────────

    void carregarCertificacoes(int page) {
        setLoading(true);

        /*
         * TODO: A API não suporta filtro por fornecedorId no endpoint
         * GET /fornecedores-certificacoes. Quando suportar, passar
         * fornecedorId como query param e remover o filtro client-side abaixo.
         */
        service.getAllCertificacoes(page, PAGE_SIZE, state -> {
            if (state.isLoading()) {
                setLoading(true);
            } else if (state.isSuccess()) {
                setLoading(false);
                var resposta = state.getData();
                if (resposta != null) {
                    // Filtrar client-side pelo fornecedorNome (a API não devolve o fornecedorId)
                    var todos = (resposta.content != null ? resposta.content : List.<FornecedorCertificacaoResponse>of())
                            .stream()
                            .filter(c -> fornecedorNome != null && fornecedorNome.equals(c.nomeFornecedor))
                            .toList();
                    currentPage = page;
                    totalPages  = Math.max(1, resposta.totalPages);
                    lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    btnAnterior.setDisable(resposta.first);
                    btnProximo.setDisable(resposta.last);
                    renderizarLista(todos);
                }
            } else if (state.isError()) {
                setLoading(false);
                listaContainer.getChildren().clear();
                var lbl = new Label("Erro ao carregar: " + state.getErrorMessage());
                lbl.getStyleClass().add("cert-erro-label");
                listaContainer.getChildren().add(lbl);
            }
        });
    }

    // ── Renderização ────────────────────────────────────────────────────────

    private void renderizarLista(List<FornecedorCertificacaoResponse> items) {
        listaContainer.getChildren().clear();

        if (items.isEmpty()) {
            var lbl = new Label("Nenhuma certificação associada.");
            lbl.getStyleClass().add("cert-vazio-label");
            listaContainer.getChildren().add(lbl);
            return;
        }

        // Header
        var header = new HBox();
        header.getStyleClass().add("tabela-header");
        header.getChildren().addAll(
                headerCol("Certificação", true),
                headerCol("Início", false),
                headerCol("Fim", false),
                headerCol("Ações", false));
        listaContainer.getChildren().add(header);

        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var row  = criarLinha(item);
            if (i == items.size() - 1) row.getStyleClass().add("tabela-linha-ultima");
            listaContainer.getChildren().add(row);
        }
    }

    private Label headerCol(String texto, boolean grow) {
        var lbl = new Label(texto.toUpperCase());
        lbl.getStyleClass().add("tabela-header-label");
        if (grow) {
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
        } else {
            lbl.setMinWidth(110);
        }
        return lbl;
    }

    private HBox criarLinha(FornecedorCertificacaoResponse item) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);

        var lblNome = new Label(item.certificacaoNome != null ? item.certificacaoNome : "—");
        lblNome.getStyleClass().add("celula-nome-principal");
        lblNome.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblNome, Priority.ALWAYS);

        var lblInicio = new Label(item.dataInicio != null ? item.dataInicio : "—");
        lblInicio.getStyleClass().add("celula-dados");
        lblInicio.setMinWidth(110);

        var lblFim = new Label(item.dataFim != null ? item.dataFim : "—");
        lblFim.getStyleClass().add("celula-dados");
        lblFim.setMinWidth(110);

        var btnEditar   = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> EditarCertificacaoModalController.show(
                item.id.toString(), item.dataInicio, item.dataFim,
                service, dialogStage,
                () -> carregarCertificacoes(currentPage)));

        btnEliminar.setOnAction(e -> EliminarCertificacaoModalController.show(
                item.id.toString(), item.certificacaoNome,
                service, dialogStage,
                () -> carregarCertificacoes(currentPage)));

        var acoes = new HBox(6, btnEditar, btnEliminar);
        acoes.setMinWidth(140);
        acoes.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(lblNome, lblInicio, lblFim, acoes);
        return row;
    }

    // ── Helpers de UI ───────────────────────────────────────────────────────

    private void setLoading(boolean loading) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setManaged(loading);
        btnAdicionarCertificacao.setDisable(loading);
    }
}
