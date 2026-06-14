package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.stock.DetalhesLoteProducaoModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoResponse;
import com.gestaoiogurtes.services.LoteProducaoService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StockController implements AppAware {

    @FXML
    private VBox tabelaContainer;
    @FXML
    private StackPane rootStack;
    @FXML
    private VBox loadingOverlay;
    @FXML
    private ComboBox<Integer> cbTamanhoPagina;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnProxima;
    @FXML
    private Label lblPagina;
    @FXML
    private Sidebar sidebarController;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private List<LoteProducaoResponse> todosItens = List.of();

    private GestaoIogurtes app;
    private LoteProducaoService service;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }

    @FXML
    public void initialize() {
        service = new LoteProducaoService();

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarItens();
                }
            });
        }
        carregarItens();
    }

    private void carregarItens() {
        service.getAll(currentPage, pageSize, state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoading(true);
                case SUCCESS -> {
                    setLoading(false);
                    var response = state.getData();
                    if (response != null) {
                        todosItens = response.content != null ? response.content : List.of();
                        this.totalPages = response.totalPages;

                        if (lblPagina != null) {
                            lblPagina.setText(
                                    "Página " + (this.currentPage + 1) + " de " + Math.max(1, this.totalPages));
                        }
                        if (btnAnterior != null)
                            btnAnterior.setDisable(response.first);
                        if (btnProxima != null)
                            btnProxima.setDisable(response.last);
                    }
                    renderizarTabela();
                }
                case ERROR -> {
                    setLoading(false);
                    mostrarNotificacao("Erro: " + state.getErrorMessage(), false);
                }
                default -> {
                }
            }
        });
    }

    private void renderizarTabela() {
        tabelaContainer.getChildren().clear();

        if (todosItens.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        tabelaContainer.getChildren().add(criarLinhaHeader());

        for (var item : todosItens) {
            tabelaContainer.getChildren().add(criarLinhaItem(item));
        }
    }

    private HBox criarLinhaHeader() {
        var row = new HBox();
        row.getStyleClass().add("tabela-header");
        row.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(
                headerCol("", 48, false),
                headerCol("Nº Lote", 150, true),
                headerCol("Produto", 250, true),
                headerCol("Stock (kg)", 120, false),
                headerCol("Validade", 120, false),
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

    private HBox criarLinhaItem(LoteProducaoResponse item) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");

        if ("GASTO".equals(item.estado)) {
            row.getStyleClass().add("linha-gasta");
        }

        row.setMaxWidth(Double.MAX_VALUE);

        var numeroLoteLabel = createLabel(item.numeroLote != null ? item.numeroLote : "—", 150, true);
        numeroLoteLabel.getStyleClass().add("celula-nome-principal");

        var produtoLabel = createLabel(item.produtoNome != null ? item.produtoNome : "—", 250, true);

        String stockStr = item.stockAtualKg != null ? String.format("%.2f", item.stockAtualKg) : "0.00";
        var stockLabel = createLabel(stockStr, 120, false);

        String validadeStr = item.dataValidade != null ? item.dataValidade.format(DATE_FORMATTER) : "—";
        var validadeLabel = createLabel(validadeStr, 120, false);

        var btnDetalhes = new Button("Detalhes");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnDetalhes.setOnAction(e -> DetalhesLoteProducaoModalController.show(
                item, service, tabelaContainer.getScene().getWindow()));

        var acoesBox = new HBox(6, btnDetalhes);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(100);

        var avatar = criarAvatar(item.produtoNome);
        row.getChildren().addAll(avatar, numeroLoteLabel, produtoLabel, stockLabel, validadeLabel, acoesBox);
        return row;
    }

    private Label createLabel(String text, double width, boolean grow) {
        var lbl = new Label(text);
        lbl.setMinWidth(width);
        if (grow) {
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
        } else {
            lbl.setPrefWidth(width);
        }
        lbl.setWrapText(true);
        return lbl;
    }

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignD.DOMAIN_OFF);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum lote de produção encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Não há lotes de produção disponíveis no stock.");
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
        if (cbTamanhoPagina != null)
            cbTamanhoPagina.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarItens();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarItens();
        }
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
