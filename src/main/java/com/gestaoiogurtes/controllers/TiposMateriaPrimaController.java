package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.tipomateriaPrima.CriarTipoMateriaPrimaModalController;
import com.gestaoiogurtes.components.tipomateriaPrima.EditarTipoMateriaPrimaModalController;
import com.gestaoiogurtes.components.tipomateriaPrima.EliminarTipoMateriaPrimaModalController;
import com.gestaoiogurtes.components.tipomateriaPrima.DetalhesTipoMateriaPrimaModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import com.gestaoiogurtes.services.TipoMateriaPrimaService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import javafx.fxml.FXML;

import java.util.List;

public class TiposMateriaPrimaController implements AppAware {

    private final TipoMateriaPrimaService service = new TipoMateriaPrimaService();

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

    private List<TipoMateriaPrimaResponse> todosItens = List.of();

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
                    carregarItens();
                }
            });
        }

        carregarItens();
    }

    @FXML
    private void handleNovo() {
        CriarTipoMateriaPrimaModalController.show(
                service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida);
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
                            lblPagina.setText("Página " + (this.currentPage + 1) + " de " + Math.max(1, this.totalPages));
                        }
                        if (btnAnterior != null) btnAnterior.setDisable(response.first);
                        if (btnProxima != null) btnProxima.setDisable(response.last);
                    } else {
                        todosItens = List.of();
                    }
                    filtrarTabela(campoPesquisa != null ? campoPesquisa.getText().toLowerCase().trim() : "");
                }

                case ERROR -> {
                    setLoading(false);
                    mostrarNotificacao("Erro ao carregar tipos de matéria prima: " + state.getErrorMessage(), false);
                }
                default -> {}
            }
        });
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

    private void filtrarTabela(String pesquisa) {
        tabelaContainer.getChildren().clear();

        var filtrados = todosItens.stream()
                .filter(e -> pesquisa.isEmpty() || (e.nome != null && e.nome.toLowerCase().contains(pesquisa)))
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
                headerCol("Nome do Tipo", 220, true),
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

    private boolean isActionButtonClicked(javafx.scene.input.MouseEvent e) {
        javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
        while (target != null && target != tabelaContainer) {
            if (target instanceof Button) return true;
            target = target.getParent();
        }
        return false;
    }

    private HBox criarLinhaTabela(TipoMateriaPrimaResponse tipo, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setCursor(javafx.scene.Cursor.HAND);

        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1 && !isActionButtonClicked(e)) {
                DetalhesTipoMateriaPrimaModalController.show(tipo, tabelaContainer.getScene().getWindow());
            }
        });

        // Avatar com iniciais
        var avatar = criarAvatar(tipo.nome, index);

        // Nome
        var nomeLabel = new Label(tipo.nome != null ? tipo.nome : "—");
        nomeLabel.getStyleClass().add("celula-nome-principal");

        var nomeBox = new VBox(2, nomeLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        // Botões de acção
        var btnEditar = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> {
            e.consume();
            EditarTipoMateriaPrimaModalController.show(tipo, service, tabelaContainer.getScene().getWindow(), this::onMutacaoBemSucedida);
        });

        btnEliminar.setOnAction(e -> {
            e.consume();
            EliminarTipoMateriaPrimaModalController.show(tipo, service, tabelaContainer.getScene().getWindow(), this::onMutacaoBemSucedida);
        });

        var acoesBox = new HBox(6, btnEditar, btnEliminar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(140);

        row.getChildren().addAll(avatar, nomeBox, acoesBox);
        return row;
    }

    private StackPane criarAvatar(String nome, int index) {
        String iniciais = extrairIniciais(nome);
        int cor = index % 4;

        var texto = new Label(iniciais);
        texto.getStyleClass().addAll("avatar-texto", "avatar-texto-cor-" + cor);

        var pane = new StackPane(texto);
        pane.getStyleClass().addAll("avatar", "avatar-cor-" + cor);
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

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignD.DOMAIN_OFF); // Can reuse domain_off or find a leaf icon
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum tipo encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou clica em \"Novo Tipo\" para adicionar.");
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
        if (btnNovo != null) btnNovo.setDisable(loading);
        if (fab != null) fab.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarItens();
    }

    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
    }
}
