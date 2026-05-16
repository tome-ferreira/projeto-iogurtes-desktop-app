package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.fornecedorestipo.CriarFornecedorTipoModalController;
import com.gestaoiogurtes.components.fornecedorestipo.EditarFornecedorTipoModalController;
import com.gestaoiogurtes.components.fornecedorestipo.EliminarFornecedorTipoModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.services.FornecedorTipoService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import javafx.fxml.FXML;

import java.util.List;

public class FornecedoresTipoController implements AppAware {

    private final FornecedorTipoService service = new FornecedorTipoService();

    @FXML private Sidebar    sidebarController;
    @FXML private VBox       tabelaContainer;
    @FXML private StackPane  rootStack;
    @FXML private VBox       loadingOverlay;
    @FXML private TextField  campoPesquisa;
    @FXML private Button     btnNovo;
    @FXML private Button     fab;

    private List<FornecedorTipoResponse> todosTipos = List.of();

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela(val.trim().toLowerCase()));
        carregarTipos();
    }

    @FXML
    private void handleNovo() {
        CriarFornecedorTipoModalController.show(
                service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida
        );
    }

    private void carregarTipos() {
        service.getAll(state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoading(true);

                case SUCCESS -> {
                    setLoading(false);
                    todosTipos = state.getData() != null ? state.getData() : List.of();
                    filtrarTabela(campoPesquisa != null ? campoPesquisa.getText().toLowerCase().trim() : "");
                }

                case ERROR -> {
                    setLoading(false);
                    mostrarNotificacao("Erro ao carregar tipos de fornecedor: " + state.getErrorMessage(), false);
                }

                default -> {} 
            }
        });
    }

    private void filtrarTabela(String pesquisa) {
        tabelaContainer.getChildren().clear();

        var filtrados = todosTipos.stream()
                .filter(t -> pesquisa.isEmpty()
                        || (t.nome != null && t.nome.toLowerCase().contains(pesquisa))
                        || (t.descricao != null && t.descricao.toLowerCase().contains(pesquisa)))
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
                headerCol("",            48,  false),
                headerCol("Nome",       220, true),
                headerCol("Ações",      140, false)
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

    private HBox criarLinhaTabela(FornecedorTipoResponse tipo, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        var avatar = criarAvatar(tipo.nome, index);

        var nomeLabel = new Label(tipo.nome);
        nomeLabel.getStyleClass().add("celula-nome-principal");

        var nomeBox = new VBox(2, nomeLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        var btnEditar   = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> EditarFornecedorTipoModalController.show(
                tipo, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        btnEliminar.setOnAction(e -> EliminarFornecedorTipoModalController.show(
                tipo, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        var acoesBox = new HBox(6, btnEditar, btnEliminar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(140);

        row.getChildren().addAll(avatar, nomeBox, acoesBox);
        
        row.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1 && e.getTarget() instanceof javafx.scene.Node node) {
                // Ensure we don't open details if an action button was clicked
                boolean isActionButton = false;
                javafx.scene.Node current = node;
                while (current != null && current != row) {
                    if (current instanceof Button) {
                        isActionButton = true;
                        break;
                    }
                    current = current.getParent();
                }
                if (!isActionButton) {
                    com.gestaoiogurtes.components.fornecedorestipo.DetalhesFornecedorTipoModalController.show(
                            tipo,
                            tabelaContainer.getScene().getWindow()
                    );
                }
            }
        });
        
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
        var icone = new FontIcon(MaterialDesignT.TAG_OFF_OUTLINE);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum tipo de fornecedor encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou clica em \"Novo tipo\" para adicionar.");
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
        if (fab     != null) fab.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarTipos();
    }

    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
    }
}
