package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.encomendaMp.CriarEncomendaMpModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;
import com.gestaoiogurtes.services.EncomendaMpService;
import com.gestaoiogurtes.services.FornecedorService;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import javafx.fxml.FXML;

import java.util.List;

/**
 * Controller da página principal de Encomendas de Matéria Prima.
 * Segue exactamente o padrão de {@code FornecedoresController}.
 *
 * <p>Funcionalidades:
 * <ul>
 *   <li>Listagem paginada (10 por página por padrão)</li>
 *   <li>Filtro por estado via ComboBox</li>
 *   <li>Botão FAB + botão de cabeçalho para criar nova encomenda</li>
 * </ul>
 */
public class EncomendaMpController implements AppAware {

    // ── Serviços ────────────────────────────────────────────────────────────
    private final EncomendaMpService  service             = new EncomendaMpService();
    private final FornecedorService   fornecedorService   = new FornecedorService();
    private final MateriaPrimaService materiaPrimaService = new MateriaPrimaService();

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private Sidebar        sidebarController;
    @FXML private VBox           tabelaContainer;
    @FXML private StackPane      rootStack;
    @FXML private VBox           loadingOverlay;
    @FXML private Button         btnNovo;
    @FXML private Button         fab;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private Label          lblPagina;
    @FXML private Button         btnAnterior;
    @FXML private Button         btnProxima;
    @FXML private ComboBox<Integer> cbTamanhoPagina;

    // ── Estado ────────────────────────────────────────────────────────────────
    private int                        currentPage  = 0;
    private int                        pageSize     = 10;
    private int                        totalPages   = 0;
    private List<EncomendaMpResponse>  todosItens   = List.of();
    private String                     selectedEstado = null;

    // ── AppAware ──────────────────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Filtro de estado
        cbFiltroEstado.getItems().clear();
        cbFiltroEstado.getItems().add("Todos os Estados");
        for (String label : EnumDisplayHelper.estadoEncomendaMpLabels()) {
            cbFiltroEstado.getItems().add(label);
        }
        cbFiltroEstado.getSelectionModel().selectFirst();
        cbFiltroEstado.valueProperty().addListener((obs, old, val) -> {
            selectedEstado = "Todos os Estados".equals(val)
                    ? null
                    : EnumDisplayHelper.estadoEncomendaMpParaApi(val);
            currentPage = 0;
            carregarEncomendas();
        });

        // Tamanho da página
        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarEncomendas();
                }
            });
        }

        carregarEncomendas();
    }

    // ── Acções ────────────────────────────────────────────────────────────────

    @FXML
    private void handleNovo() {
        CriarEncomendaMpModalController.show(
                service,
                fornecedorService,
                materiaPrimaService,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida);
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarEncomendas();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarEncomendas();
        }
    }

    // ── Carregamento de dados ─────────────────────────────────────────────────

    private void carregarEncomendas() {
        if (selectedEstado == null || selectedEstado.isBlank()) {
            service.getAll(currentPage, pageSize, state -> handleState(state));
        } else {
            service.getByEstado(selectedEstado, currentPage, pageSize, state -> handleState(state));
        }
    }

    private void handleState(com.gestaoiogurtes.api.QueryState<com.gestaoiogurtes.models.PaginatedResponse<EncomendaMpResponse>> state) {
        switch (state.getStatus()) {
            case LOADING -> setLoading(true);
            case SUCCESS -> {
                setLoading(false);
                var response = state.getData();
                if (response != null) {
                    todosItens = response.content != null ? response.content : List.of();
                    totalPages = response.totalPages;
                    if (lblPagina != null) {
                        lblPagina.setText("Página " + (currentPage + 1) + " de " + Math.max(1, totalPages));
                    }
                    if (btnAnterior != null) btnAnterior.setDisable(response.first);
                    if (btnProxima  != null) btnProxima.setDisable(response.last);
                } else {
                    todosItens = List.of();
                }
                renderizarTabela();
            }
            case ERROR -> {
                setLoading(false);
                mostrarNotificacao("Erro ao carregar encomendas: " + state.getErrorMessage(), false);
            }
            default -> {}
        }
    }

    // ── Renderização da tabela ────────────────────────────────────────────────

    private void renderizarTabela() {
        tabelaContainer.getChildren().clear();

        if (todosItens.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        tabelaContainer.getChildren().add(criarLinhaHeader());

        for (int i = 0; i < todosItens.size(); i++) {
            var linha = criarLinhaTabela(todosItens.get(i), i);
            if (i == todosItens.size() - 1) {
                linha.getStyleClass().add("tabela-linha-ultima");
            }
            tabelaContainer.getChildren().add(linha);
        }
    }

    private HBox criarLinhaHeader() {
        var row = new HBox();
        row.getStyleClass().add("tabela-header");
        row.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().addAll(
                headerCol("Fornecedor",   200, true),
                headerCol("Data",         130, false),
                headerCol("Entrega Prev.", 120, false),
                headerCol("Estado",       120, false),
                headerCol("Total (€)",    120, false),
                headerCol("Ações",        120, false));
        return row;
    }

    private Label headerCol(String texto, double largura, boolean grow) {
        var lbl = new Label(texto.toUpperCase());
        lbl.getStyleClass().add("tabela-header-label");
        lbl.setMinWidth(largura);
        if (grow) {
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
        } else {
            lbl.setPrefWidth(largura);
        }
        return lbl;
    }

    private HBox criarLinhaTabela(EncomendaMpResponse item, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Fornecedor
        var fornecedorLabel = new Label(item.fornecedorNome != null ? item.fornecedorNome : "—");
        fornecedorLabel.getStyleClass().add("celula-nome-principal");
        fornecedorLabel.setMinWidth(200);
        fornecedorLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(fornecedorLabel, Priority.ALWAYS);

        // Data encomenda (mostrar só a data, sem horas)
        String dataStr = formatarData(item.dataEncomenda);
        var dataLabel = new Label(dataStr);
        dataLabel.getStyleClass().add("celula-dados");
        dataLabel.setPrefWidth(130);

        // Data entrega prevista
        String dataEntregaStr = item.dataEntregaPrevista != null ? item.dataEntregaPrevista : "—";
        var dataEntregaLabel = new Label(dataEntregaStr);
        dataEntregaLabel.getStyleClass().add("celula-dados");
        dataEntregaLabel.setPrefWidth(120);

        // Estado (pill)
        String estadoLabel = EnumDisplayHelper.estadoEncomendaMp(item.estado);
        var estadoPill = new Label(estadoLabel);
        estadoPill.getStyleClass().addAll("pill-estado", "pill-estado-" + estadoApiParaCss(item.estado));
        estadoPill.setPrefWidth(120);

        // Total EUR (com IVA)
        String totalStr = item.totalPrecoEurComIva != null
                ? String.format("%.2f €", item.totalPrecoEurComIva)
                : "—";
        var totalLabel = new Label(totalStr);
        totalLabel.getStyleClass().add("celula-dados");
        totalLabel.setPrefWidth(120);

        // Botão Detalhes
        var btnDetalhes = new Button("Detalhes");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnDetalhes.setOnAction(e -> mostrarDetalhes(item));

        var acoesBox = new HBox(6, btnDetalhes);
        acoesBox.setAlignment(Pos.CENTER_LEFT);
        acoesBox.setMinWidth(120);

        row.getChildren().addAll(
                fornecedorLabel, dataLabel, dataEntregaLabel,
                estadoPill, totalLabel, acoesBox);
        return row;
    }

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignA.ARCHIVE_OUTLINE);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhuma encomenda encontrada");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta o filtro de estado ou clica em \"Nova Encomenda MP\" para criar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    // ── Detalhe da encomenda ──────────────────────────────────────────────────

    private void mostrarDetalhes(EncomendaMpResponse item) {
        com.gestaoiogurtes.components.encomendaMp.DetalhesEncomendaMpModalController.show(
                item,
                service,
                tabelaContainer.getScene().getWindow(),
                mensagem -> {
                    currentPage = 0;
                    onMutacaoBemSucedida(mensagem);
                });
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private String formatarData(String dataIso) {
        if (dataIso == null || dataIso.isBlank()) return "—";
        // dataEncomenda é date-time (ISO 8601): pegar só os 10 primeiros caracteres
        return dataIso.length() >= 10 ? dataIso.substring(0, 10) : dataIso;
    }

    /** Converte estado API em classe CSS válida (sem espaços, minúsculas). */
    private String estadoApiParaCss(String estado) {
        if (estado == null) return "desconhecido";
        return estado.toLowerCase().replace("_", "-");
    }

    private void setLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        }
        if (btnNovo  != null) btnNovo.setDisable(loading);
        if (fab      != null) fab.setDisable(loading);
        if (cbFiltroEstado != null) cbFiltroEstado.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarEncomendas();
    }

    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
    }
}
