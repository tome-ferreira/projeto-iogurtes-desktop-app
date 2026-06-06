package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.ordemProducao.CriarOrdemProducaoModalController;
import com.gestaoiogurtes.components.ordemProducao.DetalhesOrdemProducaoModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoResponse;
import com.gestaoiogurtes.services.OrdemProducaoService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdensProducaoController implements AppAware {

    private final OrdemProducaoService service = new OrdemProducaoService();

    @FXML private VBox tabelaContainer;
    @FXML private StackPane rootStack;
    @FXML private VBox loadingOverlay;
    @FXML private Button btnNovo;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private Label lblPagina;
    @FXML private Button btnAnterior;
    @FXML private Button btnProxima;
    @FXML private ComboBox<Integer> cbTamanhoPagina;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private List<OrdemProducaoResponse> todosItens = List.of();
    private String selectedEstado = null;

    @FXML private Sidebar sidebarController;

    private GestaoIogurtes app;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }

    @FXML
    public void initialize() {
        cbFiltroEstado.getItems().clear();
        cbFiltroEstado.getItems().add("Todos os Estados");
        for (String label : EnumDisplayHelper.estadoOrdemProducaoLabels()) {
            cbFiltroEstado.getItems().add(label);
        }
        cbFiltroEstado.getSelectionModel().selectFirst();
        cbFiltroEstado.valueProperty().addListener((obs, old, val) -> {
            selectedEstado = "Todos os Estados".equals(val)
                    ? null
                    : EnumDisplayHelper.estadoOrdemProducaoParaApi(val);
            currentPage = 0;
            carregarOrdens();
        });

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarOrdens();
                }
            });
        }

        carregarOrdens();
    }

    @FXML
    private void handleNovo() {
        CriarOrdemProducaoModalController.show(
                service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida
        );
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarOrdens();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarOrdens();
        }
    }

    private void carregarOrdens() {
        if (selectedEstado == null || selectedEstado.isBlank()) {
            service.getAll(currentPage, pageSize, state -> handleState(state));
        } else {
            service.getByEstado(selectedEstado, currentPage, pageSize, state -> handleState(state));
        }
    }

    private void handleState(com.gestaoiogurtes.api.QueryState<com.gestaoiogurtes.models.PaginatedResponse<OrdemProducaoResponse>> state) {
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
                mostrarNotificacao("Erro ao carregar ordens: " + state.getErrorMessage(), false);
            }
            default -> {}
        }
    }

    private void renderizarTabela() {
        tabelaContainer.getChildren().clear();

        if (todosItens.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        tabelaContainer.getChildren().add(criarLinhaHeader());

        for (int i = 0; i < todosItens.size(); i++) {
            var linha = criarLinhaTabela(todosItens.get(i));
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
                headerCol("Utilizador", 200, true),
                headerCol("Data de Início", 140, false),
                headerCol("Data de Fim", 140, false),
                headerCol("Estado", 130, false),
                headerCol("Ações", 100, false));
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

    private HBox criarLinhaTabela(OrdemProducaoResponse item) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        var userLabel = new Label(item.userNome != null ? item.userNome : "—");
        userLabel.getStyleClass().add("celula-nome-principal");
        userLabel.setMinWidth(200);
        userLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(userLabel, Priority.ALWAYS);

        var inicioLabel = new Label(formatarData(item.dataInicio));
        inicioLabel.getStyleClass().add("celula-dados");
        inicioLabel.setPrefWidth(140);

        var fimLabel = new Label(formatarData(item.dataFim));
        fimLabel.getStyleClass().add("celula-dados");
        fimLabel.setPrefWidth(140);

        var estadoPill = new Label(EnumDisplayHelper.estadoOrdemProducao(item.estado));
        estadoPill.getStyleClass().addAll("pill-ordem-estado", "pill-estado-" + estadoApiParaCss(item.estado));
        estadoPill.setPrefWidth(130);

        var btnDetalhes = new Button("Detalhes");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnDetalhes.setOnAction(e -> mostrarDetalhes(item));

        var acoesBox = new HBox(6, btnDetalhes);
        acoesBox.setAlignment(Pos.CENTER_LEFT);
        acoesBox.setMinWidth(100);

        row.getChildren().addAll(userLabel, inicioLabel, fimLabel, estadoPill, acoesBox);
        return row;
    }

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignA.ARCHIVE_OUTLINE);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhuma ordem de produção encontrada");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta o filtro de estado ou clica em \"Nova Ordem\" para criar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    private void mostrarDetalhes(OrdemProducaoResponse item) {
        DetalhesOrdemProducaoModalController.show(
                item,
                service,
                tabelaContainer.getScene().getWindow(),
                mensagem -> {
                    currentPage = 0;
                    onMutacaoBemSucedida(mensagem);
                }
        );
    }

    private String formatarData(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(FORMATTER);
    }

    private String estadoApiParaCss(String estado) {
        if (estado == null) return "desconhecido";
        return estado.toLowerCase().replace("_", "-");
    }

    private void setLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        }
        if (btnNovo != null) btnNovo.setDisable(loading);
        if (cbFiltroEstado != null) cbFiltroEstado.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        currentPage = 0;
        carregarOrdens();
    }
}
