package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.moeda.CriarMoedaModalController;
import com.gestaoiogurtes.components.moeda.EditarMoedaModalController;
import com.gestaoiogurtes.components.moeda.EliminarMoedaModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.services.MoedaService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import javafx.fxml.FXML;

import java.util.List;

public class MoedasController implements AppAware {

    private final MoedaService service = new MoedaService();

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

    private int currentPage = 0;
    private int pageSize = 20;
    private int totalPages = 0;

    private List<MoedaResponse> todasMoedas = List.of();

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela(val.trim().toLowerCase()));

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarMoedas();
                }
            });
        }

        carregarMoedas();
    }

    @FXML
    private void handleNovo() {
        CriarMoedaModalController.show(
                service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida);
    }

    private void carregarMoedas() {
        service.getAll(currentPage, pageSize, state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoading(true);

                case SUCCESS -> {
                    setLoading(false);
                    var response = state.getData();
                    if (response != null) {
                        todasMoedas = response.content != null ? response.content : List.of();
                        this.totalPages = response.totalPages;

                        if (lblPagina != null) {
                            lblPagina.setText(
                                    "Página " + (this.currentPage + 1) + " de " + Math.max(1, this.totalPages));
                        }
                        if (btnAnterior != null)
                            btnAnterior.setDisable(response.first);
                        if (btnProxima != null)
                            btnProxima.setDisable(response.last);
                    } else {
                        todasMoedas = List.of();
                    }
                    filtrarTabela(campoPesquisa != null ? campoPesquisa.getText().toLowerCase().trim() : "");
                }

                case ERROR -> {
                    setLoading(false);
                    mostrarNotificacao("Erro ao carregar moedas: " + state.getErrorMessage(), false);
                }

                default -> {}
            }
        });
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarMoedas();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarMoedas();
        }
    }

    private void filtrarTabela(String pesquisa) {
        tabelaContainer.getChildren().clear();

        var filtrados = todasMoedas.stream()
                .filter(m -> pesquisa.isEmpty()
                        || m.nome.toLowerCase().contains(pesquisa)
                        || m.codigo.toLowerCase().contains(pesquisa)
                        || m.simbolo.toLowerCase().contains(pesquisa))
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

    private HBox criarLinhaHeader() {
        var row = new HBox();
        row.getStyleClass().add("tabela-header");
        row.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(
                headerCol("", 48, false),
                headerCol("Moeda", 220, true),
                headerCol("Código ISO", 120, false),
                headerCol("Símbolo", 120, false),
                headerCol("Taxa (EUR)", 120, false),
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

    private HBox criarLinhaTabela(MoedaResponse moeda, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Nome
        var nomeLabel = new Label(moeda.nome);
        nomeLabel.getStyleClass().add("celula-nome-principal");
        var nomeBox = new VBox(2, nomeLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        // Código
        var codigoLabel = new Label(moeda.codigo);
        codigoLabel.getStyleClass().add("celula-dados");
        codigoLabel.setMinWidth(120);

        // Símbolo
        var simboloLabel = new Label(moeda.simbolo);
        simboloLabel.getStyleClass().add("celula-dados");
        simboloLabel.setMinWidth(120);

        // Taxa
        var taxaLabel = new Label(moeda.taxaConversaoEur != null ? String.format("%.4f", moeda.taxaConversaoEur) : "—");
        taxaLabel.getStyleClass().add("celula-dados");
        taxaLabel.setMinWidth(120);

        // Botões de acção
        var btnEditar = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> EditarMoedaModalController.show(
                moeda, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        btnEliminar.setOnAction(e -> EliminarMoedaModalController.show(
                moeda, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        var acoesBox = new HBox(6, btnEditar, btnEliminar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(140);

        var avatar = criarAvatar(moeda.simbolo);
        row.getChildren().addAll(avatar, nomeBox, codigoLabel, simboloLabel, taxaLabel, acoesBox);
        return row;
    }

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignC.CURRENCY_USD_OFF);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhuma moeda encontrada");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou clica em \"Nova moeda\" para adicionar.");
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
        if (btnNovo != null)
            btnNovo.setDisable(loading);
        if (fab != null)
            fab.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarMoedas();
    }

    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
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
