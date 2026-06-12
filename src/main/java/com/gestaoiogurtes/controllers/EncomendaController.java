package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.encomenda.DetalhesEncomendaModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.encomenda.EncomendaResponse;
import com.gestaoiogurtes.services.EncomendaService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EncomendaController implements AppAware {

    private final EncomendaService service = new EncomendaService();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Sidebar sidebarController;
    @FXML private VBox tabelaContainer;
    @FXML private StackPane rootStack;
    @FXML private VBox loadingOverlay;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private Button btnAnterior;
    @FXML private Button btnProxima;
    @FXML private Label lblPagina;
    @FXML private ComboBox<Integer> cbTamanhoPagina;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;

    private List<EncomendaResponse> todasEncomendas = List.of();

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        if (cbFiltroEstado != null) {
            cbFiltroEstado.getItems().add("Todos os Estados");
            for (String label : EnumDisplayHelper.estadoEncomendaLabels()) {
                cbFiltroEstado.getItems().add(label);
            }
            cbFiltroEstado.setValue("Todos os Estados");
            
            cbFiltroEstado.valueProperty().addListener((obs, old, val) -> {
                currentPage = 0;
                carregarEncomendas();
            });
        }

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

    private void carregarEncomendas() {
        String estadoSelecionado = cbFiltroEstado.getValue();
        String estadoApi = null;
        if (estadoSelecionado != null && !estadoSelecionado.equals("Todos os Estados")) {
            estadoApi = EnumDisplayHelper.estadoEncomendaParaApi(estadoSelecionado);
        }

        if (estadoApi == null) {
            service.getAll(currentPage, pageSize, state -> processarResposta(state));
        } else {
            service.getByEstado(estadoApi, currentPage, pageSize, state -> processarResposta(state));
        }
    }

    private void processarResposta(com.gestaoiogurtes.api.QueryState<com.gestaoiogurtes.models.encomenda.PageEncomendaResponse> state) {
        switch (state.getStatus()) {
            case LOADING -> setLoading(true);
            case SUCCESS -> {
                setLoading(false);
                var response = state.getData();
                if (response != null) {
                    todasEncomendas = response.content != null ? response.content : List.of();
                    this.totalPages = response.totalPages;

                    if (lblPagina != null) {
                        lblPagina.setText("Página " + (this.currentPage + 1) + " de " + Math.max(1, this.totalPages));
                    }
                    if (btnAnterior != null) btnAnterior.setDisable(response.first);
                    if (btnProxima != null) btnProxima.setDisable(response.last);
                } else {
                    todasEncomendas = List.of();
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

    private void renderizarTabela() {
        tabelaContainer.getChildren().clear();

        if (todasEncomendas.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        tabelaContainer.getChildren().add(criarLinhaHeader());

        for (int i = 0; i < todasEncomendas.size(); i++) {
            var linha = criarLinhaTabela(todasEncomendas.get(i), i);
            if (i == todasEncomendas.size() - 1) {
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
                headerCol("", 48, false),
                headerCol("Data", 140, false),
                headerCol("Utilizador", 200, true),
                headerCol("Total", 120, false),
                headerCol("Estado", 120, false),
                headerCol("Ações", 100, false)
        );
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

    private HBox criarLinhaTabela(EncomendaResponse encomenda, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        String dataFormatada = encomenda.dataEncomenda != null ? encomenda.dataEncomenda.format(formatter) : "—";
        var dataLabel = new Label(dataFormatada);
        dataLabel.getStyleClass().add("celula-dados");
        dataLabel.setMinWidth(140);

        var utilizadorLabel = new Label(encomenda.userNome != null ? encomenda.userNome : "—");
        utilizadorLabel.getStyleClass().add("celula-nome-principal");
        utilizadorLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(utilizadorLabel, Priority.ALWAYS);
        utilizadorLabel.setMinWidth(200);

        String totalFormatado = encomenda.totalPrecoEur != null ? String.format("%.2f €", encomenda.totalPrecoEur) : "0.00 €";
        var totalLabel = new Label(totalFormatado);
        totalLabel.getStyleClass().add("celula-dados");
        totalLabel.setMinWidth(120);

        var estadoLabel = new Label(EnumDisplayHelper.estadoEncomenda(encomenda.estado));
        estadoLabel.getStyleClass().add("celula-estado");
        if (encomenda.estado != null) {
            estadoLabel.getStyleClass().add("estado-" + encomenda.estado.toLowerCase());
        }
        estadoLabel.setMinWidth(120);

        var btnDetalhes = new Button("Detalhes");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnDetalhes.setOnAction(e -> DetalhesEncomendaModalController.show(
                encomenda, service,
                tabelaContainer.getScene().getWindow(),
                mensagem -> {
                    currentPage = 0;
                    carregarEncomendas();
                    mostrarNotificacao(mensagem, true);
                }
        ));

        var acoesBox = new HBox(6, btnDetalhes);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(100);

        var avatar = criarAvatar(encomenda.userNome);
        row.getChildren().addAll(avatar, dataLabel, utilizadorLabel, totalLabel, estadoLabel, acoesBox);
        return row;
    }

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignD.DATABASE_REMOVE);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhuma encomenda encontrada");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Não existem encomendas para o filtro selecionado.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    private void setLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        }
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    private javafx.scene.layout.StackPane criarAvatar(String nome) {
        String iniciais = extrairIniciais(nome);
        int cor = Math.abs((nome != null ? nome : "").hashCode()) % 4;

        var texto = new javafx.scene.control.Label(iniciais);
        texto.getStyleClass().addAll("avatar-texto", "avatar-texto-cor-" + cor);

        var pane = new javafx.scene.layout.StackPane(texto);
        pane.getStyleClass().addAll("avatar", "avatar-cor-" + cor);
        javafx.scene.layout.HBox.setMargin(pane, new javafx.geometry.Insets(0, 12, 0, 0));
        return pane;
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) return "?";
        var partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return (partes[0].charAt(0) + "" + partes[partes.length - 1].charAt(0)).toUpperCase();
    }

}
