package com.gestaoiogurtes.paginas;

import atlantafx.base.theme.Styles;
import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.api.iogurtes.IIogurtesApiService;
import com.gestaoiogurtes.api.iogurtes.IogurtesApiServiceFactory;
import com.gestaoiogurtes.components.iogurtes.CriarIogurteModal;
import com.gestaoiogurtes.components.iogurtes.DetalhesIogurteModal;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.model.IogurteVM;
import com.gestaoiogurtes.utils.AppAware;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

/**
 * Controller for Iogurtes.fxml.
 *
 * <p>The FXML owns all <em>static</em> structure: page header, filter toolbar,
 * the empty table-card shell ({@code tabelaContainer}), and the FAB.
 *
 * <p>This class owns all <em>dynamic</em> rendering (filtering, row building,
 * avatars, empty-state) because it is data-driven and cannot be described
 * statically in FXML.  No layout building code has been removed — it has
 * been relocated here from what was previously in the constructor.
 */
public class Iogurtes implements AppAware {

    // ── Service ───────────────────────────────────────────────────
    private final IIogurtesApiService api = IogurtesApiServiceFactory.getInstance();

    // ── FXML references ───────────────────────────────────────────
    @FXML private Sidebar sidebarController;
    @FXML private VBox    tabelaContainer;
    @FXML private TextField campoPesquisa;
    @FXML private ComboBox<String> comboEstado;

    // ── AppAware ──────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    // ── Lifecycle ─────────────────────────────────────────────────

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList(
                "Todos os estados", "Visível", "Não visível", "Stock baixo"));
        comboEstado.setValue("Todos os estados");

        campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela());
        comboEstado.setOnAction(e -> filtrarTabela());

        renderizarTabela();
    }

    // ── FXML handlers ─────────────────────────────────────────────

    @FXML
    private void handleNovo() {
        abrirModalCriar();
    }

    // ── Renderização da tabela ────────────────────────────────────

    private void renderizarTabela() {
        filtrarTabela();
    }

    private void filtrarTabela() {
        tabelaContainer.getChildren().clear();

        var todos = api.listarTodos();
        String pesquisa = campoPesquisa != null
                ? campoPesquisa.getText().toLowerCase().trim()
                : "";
        String estado = comboEstado != null
                ? comboEstado.getValue()
                : "Todos os estados";

        var filtrados = todos.stream()
                .filter(i -> pesquisa.isEmpty()
                        || i.nome.toLowerCase().contains(pesquisa)
                        || i.codigoSku.toLowerCase().contains(pesquisa))
                .filter(i -> switch (estado) {
                    case "Visível"     -> i.visivelCliente;
                    case "Não visível" -> !i.visivelCliente;
                    case "Stock baixo" -> i.stockAtual < 20;
                    default            -> true;
                })
                .toList();

        if (filtrados.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        tabelaContainer.getChildren().add(criarLinhaHeader());

        for (int i = 0; i < filtrados.size(); i++) {
            var linha = criarLinhaTabela(filtrados.get(i), i);
            // última linha sem border-bottom
            if (i == filtrados.size() - 1) {
                linha.getStyleClass().add("tabela-linha-ultima");
            }
            tabelaContainer.getChildren().add(linha);
        }
    }

    // ── Header da tabela ──────────────────────────────────────────

    private HBox criarLinhaHeader() {
        var row = new HBox();
        row.getStyleClass().add("tabela-header");
        row.setMaxWidth(Double.MAX_VALUE);

        var colAvatar = headerCol("",         48,  false);
        var colNome   = headerCol("Nome",     220, true);   // grows
        var colVal    = headerCol("Validade", 90,  false);
        var colPreco  = headerCol("Preço",    90,  false);
        var colStock  = headerCol("Stock",    80,  false);
        var colVis    = headerCol("Visível",  90,  false);
        var colAcoes  = headerCol("Ações",    110, false);

        row.getChildren().addAll(colAvatar, colNome, colVal, colPreco, colStock, colVis, colAcoes);
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

    // ── Linha de dados ────────────────────────────────────────────

    private HBox criarLinhaTabela(IogurteVM iogurte, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Avatar
        var avatar = criarAvatar(iogurte.nome, index);

        // Nome (coluna principal)
        var nomeLabel = new Label(iogurte.nome);
        nomeLabel.getStyleClass().add("celula-nome-principal");

        var skuLabel = new Label(iogurte.codigoSku);
        skuLabel.getStyleClass().add("celula-nome-subtitulo");

        var nomeBox = new VBox(2, nomeLabel, skuLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        // Validade
        var validadeLabel = new Label(iogurte.validadeDias + " dias");
        validadeLabel.getStyleClass().add("celula-dados");
        validadeLabel.setMinWidth(90);

        // Preço (alinhado à direita)
        var precoLabel = new Label(String.format("%.2f €", iogurte.precoVenda));
        precoLabel.getStyleClass().add("celula-dados");
        precoLabel.setMinWidth(90);
        precoLabel.setAlignment(Pos.CENTER_RIGHT);

        // Stock
        var stockLabel = new Label(String.valueOf(iogurte.stockAtual));
        stockLabel.setMinWidth(80);
        stockLabel.setAlignment(Pos.CENTER_RIGHT);
        if (iogurte.stockAtual < 20) {
            stockLabel.getStyleClass().addAll("celula-dados", "celula-alerta");
        } else {
            stockLabel.getStyleClass().add("celula-dados");
        }

        // Badge visível/não-visível
        var badgeVisivel = new Label(iogurte.visivelCliente ? "Visível" : "Não visível");
        badgeVisivel.getStyleClass().addAll(
                "badge",
                iogurte.visivelCliente ? "badge-ativo" : "badge-inativo");
        badgeVisivel.setMinWidth(90);

        // Botão de detalhe
        var btnDetalhes = new Button("Ver");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnDetalhes.setMinWidth(110);
        btnDetalhes.setOnAction(e -> abrirModalDetalhes(iogurte));

        row.getChildren().addAll(
                avatar, nomeBox, validadeLabel, precoLabel,
                stockLabel, badgeVisivel, btnDetalhes);
        return row;
    }

    // ── Avatar com iniciais ───────────────────────────────────────

    private StackPane criarAvatar(String nome, int index) {
        String iniciais = extrairIniciais(nome);
        int cor = index % 4;

        var texto = new Label(iniciais);
        texto.getStyleClass().addAll("avatar-texto", "avatar-texto-cor-" + cor);

        var pane = new StackPane(texto);
        pane.getStyleClass().addAll("avatar", "avatar-cor-" + cor);

        // margem à direita para separar do nome
        HBox.setMargin(pane, new Insets(0, 12, 0, 0));
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

    // ── Estado vazio ──────────────────────────────────────────────

    private VBox criarEstadoVazio() {
        var icone = icone(MaterialDesignE.EMOTICON_SAD_OUTLINE, 52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum iogurte encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou clica em \"Novo iogurte\" para adicionar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    // ── Utilitário de ícone ───────────────────────────────────────

    private FontIcon icone(org.kordamp.ikonli.Ikon codigo, int tamanho) {
        var fi = new FontIcon(codigo);
        fi.setIconSize(tamanho);
        return fi;
    }

    // ── Abrir modais ──────────────────────────────────────────────

    private void abrirModalCriar() {
        new CriarIogurteModal(api, this::renderizarTabela)
                .show(tabelaContainer.getScene().getWindow());
    }

    private void abrirModalDetalhes(IogurteVM iogurte) {
        new DetalhesIogurteModal(iogurte, api, this::renderizarTabela)
                .show(tabelaContainer.getScene().getWindow());
    }
}