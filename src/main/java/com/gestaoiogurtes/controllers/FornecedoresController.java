package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.fornecedor.*;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.services.FornecedorService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.DynamicColorHelper;
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

public class FornecedoresController implements AppAware {

    private final FornecedorService service = new FornecedorService();

    @FXML private Sidebar sidebarController;
    @FXML private VBox tabelaContainer;
    @FXML private StackPane rootStack;
    @FXML private VBox loadingOverlay;
    @FXML private Button btnNovo;
    @FXML private Button fab;
    @FXML private ComboBox<FornecedorTipoWrapper> cbFiltroTipo;
    @FXML private Label lblPagina;
    @FXML private Button btnAnterior;
    @FXML private Button btnProxima;
    @FXML private ComboBox<Integer> cbTamanhoPagina;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private List<FornecedorResponse> todosItens = List.of();
    private String selectedTipoId = null;
    private DynamicColorHelper colorHelper;

    private static class FornecedorTipoWrapper {
        public String id;
        public String nome;

        public FornecedorTipoWrapper(String id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        @Override
        public String toString() {
            return nome;
        }
    }

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        colorHelper = new DynamicColorHelper();
        carregarTipos();
        
        cbFiltroTipo.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                selectedTipoId = val.id;
                currentPage = 0;
                carregarFornecedores();
            }
        });

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarFornecedores();
                }
            });
        }

        carregarFornecedores();
    }

    private void carregarTipos() {
        cbFiltroTipo.getItems().clear();
        cbFiltroTipo.getItems().add(new FornecedorTipoWrapper(null, "Todos os Tipos"));
        cbFiltroTipo.getSelectionModel().selectFirst();
        
        service.getAllTipos(0, 1000, state -> {
            if (state.isSuccess() && state.getData() != null && state.getData().content != null) {
                for (FornecedorTipoResponse tipo : state.getData().content) {
                    cbFiltroTipo.getItems().add(new FornecedorTipoWrapper(tipo.id.toString(), tipo.nome));
                }
            }
        });
    }

    @FXML
    private void handleNovo() {
        CriarFornecedorModalController.show(
                service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida);
    }

    private void carregarFornecedores() {
        if (selectedTipoId == null || selectedTipoId.isBlank()) {
            service.getAll(currentPage, pageSize, state -> handleState(state));
        } else {
            service.getByTipo(selectedTipoId, currentPage, pageSize, state -> handleState(state));
        }
    }

    private void handleState(com.gestaoiogurtes.api.QueryState<com.gestaoiogurtes.models.PaginatedResponse<FornecedorResponse>> state) {
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
                renderizarTabela();
            }
            case ERROR -> {
                setLoading(false);
                mostrarNotificacao("Erro ao carregar fornecedores: " + state.getErrorMessage(), false);
            }
            default -> {}
        }
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarFornecedores();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarFornecedores();
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
                headerCol("Nome", 220, true),
                headerCol("Email", 220, true),
                headerCol("Tipo", 150, false),
                headerCol("Ações", 400, false));
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

    private HBox criarLinhaTabela(FornecedorResponse item, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Nome
        var nomeLabel = new Label(item.nome != null ? item.nome : "—");
        nomeLabel.getStyleClass().add("celula-nome-principal");
        nomeLabel.setMinWidth(220);
        nomeLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeLabel, Priority.ALWAYS);

        // Email
        var emailLabel = new Label(item.email != null ? item.email : "—");
        emailLabel.getStyleClass().add("celula-dados");
        emailLabel.setMinWidth(220);
        emailLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(emailLabel, Priority.ALWAYS);

        // Tipo
        var tipoNome = (item.tipo != null && item.tipo.nome != null) ? item.tipo.nome : "Sem Tipo";
        var tipoLabel = new Label(tipoNome);
        tipoLabel.getStyleClass().add("pill-fornecedor-tipo");
        String cor = colorHelper.getColorForType(tipoNome);
        tipoLabel.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        var tipoBox = new HBox(tipoLabel);
        tipoBox.setAlignment(Pos.CENTER_LEFT);
        tipoBox.setMinWidth(150);

        // Botões de acção
        var btnDetalhes = new Button("Detalhes");
        var btnEditar = new Button("Editar");
        var btnCertificacoes = new Button("Certificações");
        var btnEliminar = new Button("Eliminar");
        
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnCertificacoes.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnDetalhes.setOnAction(e -> DetalhesFornecedorModalController.show(
                item,
                tabelaContainer.getScene().getWindow()));

        btnEditar.setOnAction(e -> EditarFornecedorModalController.show(
                item, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));
                
        btnCertificacoes.setOnAction(e -> CertificacoesFornecedorModalController.show(
                item.id.toString(),
                item.nome != null ? item.nome : "Fornecedor",
                service,
                tabelaContainer.getScene().getWindow()));

        btnEliminar.setOnAction(e -> EliminarFornecedorModalController.show(
                item, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        var acoesBox = new HBox(6, btnDetalhes, btnEditar, btnCertificacoes, btnEliminar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(400);

        row.getChildren().addAll(nomeLabel, emailLabel, tipoBox, acoesBox);
        return row;
    }

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignD.DOMAIN_OFF);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum fornecedor encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta o filtro ou clica em \"Criar Fornecedor\" para adicionar.");
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
        if (cbFiltroTipo != null) cbFiltroTipo.setDisable(loading);
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarFornecedores();
    }

    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
    }
}
