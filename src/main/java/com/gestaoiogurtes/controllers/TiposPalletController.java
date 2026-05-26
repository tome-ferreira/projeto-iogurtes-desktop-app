package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.tipoPallet.CriarTipoPalletModalController;
import com.gestaoiogurtes.components.tipoPallet.EditarTipoPalletModalController;
import com.gestaoiogurtes.components.tipoPallet.EliminarTipoPalletModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.tipoPallet.TipoPalletResponse;
import com.gestaoiogurtes.services.TipoPalletService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import java.util.List;

public class TiposPalletController implements AppAware {

    // ── Serviço ───────────────────────────────────────────────────────────────
    private final TipoPalletService service = new TipoPalletService();

    // ── FXML references ───────────────────────────────────────────────────────
    @FXML private Sidebar sidebarController;
    @FXML private VBox tabelaContainer;
    @FXML private StackPane rootStack;
    @FXML private VBox loadingOverlay;
    @FXML private TextField campoPesquisa;
    @FXML private Button btnNovo;
    @FXML private Button fab;
    @FXML private Button btnAnterior;
    @FXML private Button btnProxima;
    @FXML private Label lblPagina;
    @FXML private ComboBox<Integer> cbTamanhoPagina;

    // ── Estado local ──────────────────────────────────────────────────────────
    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private List<TipoPalletResponse> todosTipos = List.of();

    // ── AppAware ──────────────────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        if (campoPesquisa != null) {
            campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela(val.trim().toLowerCase()));
        }

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarTiposPallet();
                }
            });
        }

        carregarTiposPallet();
    }

    // ── FXML handlers ─────────────────────────────────────────────────────────

    @FXML
    private void handleNovo() {
        CriarTipoPalletModalController.show(
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida,
                this::onMutacaoComErro);
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarTiposPallet();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarTiposPallet();
        }
    }

    // ── Carregamento de dados ─────────────────────────────────────────────────

    private void carregarTiposPallet() {
        service.getAll(currentPage, pageSize, state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoading(true);

                case SUCCESS -> {
                    setLoading(false);
                    var response = state.getData();
                    if (response != null) {
                        todosTipos = response.content != null ? response.content : List.of();
                        this.totalPages = response.totalPages;

                        if (lblPagina != null) {
                            lblPagina.setText("Página " + (this.currentPage + 1) + " de " + Math.max(1, this.totalPages));
                        }
                        if (btnAnterior != null) btnAnterior.setDisable(response.first);
                        if (btnProxima != null) btnProxima.setDisable(response.last);
                    } else {
                        todosTipos = List.of();
                    }
                    filtrarTabela(campoPesquisa != null ? campoPesquisa.getText().toLowerCase().trim() : "");
                }

                case ERROR -> {
                    setLoading(false);
                    mostrarNotificacao("Erro ao carregar tipos de pallet: " + state.getErrorMessage(), false);
                }

                default -> {} // IDLE — ignorar
            }
        });
    }

    // ── Filtros ───────────────────────────────────────────────────────────────

    private void filtrarTabela(String pesquisa) {
        tabelaContainer.getChildren().clear();

        var filtrados = todosTipos.stream()
                .filter(t -> pesquisa.isEmpty()
                        || (t.nome != null && t.nome.toLowerCase().contains(pesquisa))
                        || (t.capacidadeKg != null && t.capacidadeKg.toString().contains(pesquisa)))
                .toList();

        if (filtrados.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        tabelaContainer.getChildren().add(criarLinhaHeader());

        for (int i = 0; i < filtrados.size(); i++) {
            var linha = criarLinhaTabela(filtrados.get(i), i);
            if (i == filtrados.size() - 1) {
                linha.getStyleClass().add("tabela-linha-ultima");
            }
            tabelaContainer.getChildren().add(linha);
        }
    }

    // ── Header da tabela ──────────────────────────────────────────────────────

    private HBox criarLinhaHeader() {
        var row = new HBox();
        row.getStyleClass().add("tabela-header");
        row.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(
                headerCol("", 48, false),
                headerCol("Nome", 220, true),
                headerCol("Capacidade (Kg)", 140, false),
                headerCol("Ações", 140, false));
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

    // ── Linhas de dados ───────────────────────────────────────────────────────

    private HBox criarLinhaTabela(TipoPalletResponse tipo, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Avatar com iniciais
        var avatar = criarAvatar(tipo.nome, index);

        // Nome (coluna principal)
        var nomeLabel = new Label(tipo.nome != null ? tipo.nome : "—");
        nomeLabel.getStyleClass().add("celula-nome-principal");

        var nomeBox = new VBox(2, nomeLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        // Capacidade
        String capStr = tipo.capacidadeKg != null ? tipo.capacidadeKg + " Kg" : "—";
        var capacidadeLabel = new Label(capStr);
        capacidadeLabel.getStyleClass().add("celula-dados");
        capacidadeLabel.setMinWidth(140);

        // Botões de acção
        var btnEditar = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> EditarTipoPalletModalController.show(
                tipo,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida,
                this::onMutacaoComErro));

        btnEliminar.setOnAction(e -> EliminarTipoPalletModalController.show(
                tipo,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida,
                this::onMutacaoComErro));

        var acoesBox = new HBox(6, btnEditar, btnEliminar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(140);

        row.getChildren().addAll(avatar, nomeBox, capacidadeLabel, acoesBox);
        return row;
    }

    // ── Avatar ────────────────────────────────────────────────────────────────

    private StackPane criarAvatar(String nome, int index) {
        String iniciais = extrairIniciais(nome);
        int cor = index % 4;

        var texto = new Label(iniciais);
        texto.getStyleClass().addAll("avatar-texto", "avatar-texto-cor-" + cor);

        var pane = new StackPane(texto);
        pane.getStyleClass().addAll("avatar", "avatar-cor-" + cor);
        HBox.setMargin(pane, new javafx.geometry.Insets(0, 12, 0, 0));
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

    // ── Estado vazio ──────────────────────────────────────────────────────────

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignP.PACKAGE_VARIANT_CLOSED);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum tipo de pallet encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou clica em \"Novo Tipo de Pallet\" para adicionar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    // ── Loading overlay ───────────────────────────────────────────────────────

    private void setLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        }
        if (btnNovo != null) btnNovo.setDisable(loading);
        if (fab != null) fab.setDisable(loading);
    }

    // ── Mensagens de feedback ─────────────────────────────────────────────────

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    // ── Callbacks após mutação ────────────────────────────────────────────────

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarTiposPallet();
    }

    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
    }
}
