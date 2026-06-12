package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.produtoFinal.CriarProdutoFinalModalController;
import com.gestaoiogurtes.components.produtoFinal.DetalhesProdutoFinalModalController;
import com.gestaoiogurtes.components.produtoFinal.EditarProdutoFinalModalController;
import com.gestaoiogurtes.components.produtoFinal.EliminarProdutoFinalModalController;
import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.ProdutoFinalService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

/**
 * Controller da página Produtos Finais.
 * Segue o mesmo padrão do MateriasPrimasController.
 */
public class ProdutosFinaisController implements AppAware {

    @FXML private StackPane rootStack;
    @FXML private VBox      tabelaContainer;
    @FXML private VBox      loadingOverlay;
    @FXML private Button    btnNovo;
    @FXML private Button    fab;

    @FXML private Label  lblPagina;
    @FXML private Button btnAnterior;
    @FXML private Button btnProxima;
    @FXML private javafx.scene.control.ComboBox<Integer> cbTamanhoPagina;

    @FXML private com.gestaoiogurtes.layout.Sidebar sidebarController;

    private ProdutoFinalService service;
    private MateriaPrimaService materiaPrimaService;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;

    @Override
    public void setApp(GestaoIogurtes app) {
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }

    @FXML
    public void initialize() {
        service = new ProdutoFinalService();
        materiaPrimaService = new MateriaPrimaService();

        String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();
        if ("FUNCIONARIO_MP".equals(role) || "FUNCIONARIO_OP".equals(role)) {
            if (btnNovo != null) {
                btnNovo.setVisible(false);
                btnNovo.setManaged(false);
            }
            if (fab != null) {
                fab.setVisible(false);
                fab.setManaged(false);
            }
        }

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPagina.setValue(pageSize);
            cbTamanhoPagina.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSize) {
                    pageSize = val;
                    currentPage = 0;
                    carregarDados();
                }
            });
        }

        carregarDados();
    }

    private void carregarDados() {
        setLoading(true);
        tabelaContainer.getChildren().clear();

        service.getAll(currentPage, pageSize, state -> {
            if (state.isLoading()) {
                setLoading(true);
            } else if (state.isSuccess()) {
                setLoading(false);
                var response = state.getData();
                if (response != null) {
                    totalPages = Math.max(1, response.totalPages);
                    if (lblPagina != null) {
                        lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    }
                    if (btnAnterior != null) btnAnterior.setDisable(response.first);
                    if (btnProxima  != null) btnProxima.setDisable(response.last);

                    renderizarTabela(response.content != null ? response.content : List.of());
                }
            } else if (state.isError()) {
                setLoading(false);
                mostrarErro("Erro ao carregar produtos finais: " + state.getErrorMessage());
            }
        });
    }

    private void renderizarTabela(List<ProdutoFinalResponse> itens) {
        if (itens.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        // Cabeçalho
        HBox header = new HBox();
        header.getStyleClass().add("tabela-header");
        header.setMaxWidth(Double.MAX_VALUE);
        header.getChildren().addAll(
                headerCol("CÓDIGO SKU", 160, false),
                headerCol("NOME", 220, true),
                headerCol("AÇÕES", 300, false)
        );
        tabelaContainer.getChildren().add(header);

        for (int i = 0; i < itens.size(); i++) {
            ProdutoFinalResponse p = itens.get(i);

            HBox row = new HBox();
            row.getStyleClass().add("tabela-linha");
            if (i == itens.size() - 1) {
                row.getStyleClass().add("tabela-linha-ultima");
            }
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);

            // Código SKU
            Label lblSku = new Label(p.codigoSku != null ? p.codigoSku : "—");
            lblSku.getStyleClass().add("celula-dados");
            lblSku.setMinWidth(160);
            lblSku.setPrefWidth(160);

            // Nome
            Label lblNome = new Label(p.nome != null ? p.nome : "—");
            lblNome.getStyleClass().add("celula-nome-principal");
            lblNome.setMinWidth(220);
            lblNome.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            // Ações (text buttons only — never icon buttons)
            HBox colAcoes = new HBox(6);
            colAcoes.setMinWidth(300);
            colAcoes.setAlignment(Pos.CENTER_RIGHT);

            Button btnDetalhes = new Button("Detalhes");
            btnDetalhes.getStyleClass().add("btn-linha-acao");
            btnDetalhes.setOnAction(e ->
                    DetalhesProdutoFinalModalController.show(p, btnDetalhes.getScene().getWindow()));

            Button btnEditar = new Button("Editar");
            btnEditar.getStyleClass().add("btn-linha-acao");
            btnEditar.setOnAction(e ->
                    EditarProdutoFinalModalController.show(p, service, btnEditar.getScene().getWindow(), msg -> {
                        mostrarSucesso(msg);
                        carregarDados();
                    }));

            Button btnComposicao = new Button("Composição");
            btnComposicao.getStyleClass().add("btn-linha-acao");
            btnComposicao.setOnAction(e ->
                    com.gestaoiogurtes.components.produtoFinal.ComposicaoModalController.show(
                            p.id.toString(), p.nome, service, materiaPrimaService, btnComposicao.getScene().getWindow()
                    ));


            Button btnEliminar = new Button("Eliminar");
            btnEliminar.getStyleClass().add("btn-linha-danger");
            btnEliminar.setOnAction(e ->
                    EliminarProdutoFinalModalController.show(p, service, btnEliminar.getScene().getWindow(), msg -> {
                        mostrarSucesso(msg);
                        carregarDados();
                    }));

            String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();
            boolean isFuncionario = "FUNCIONARIO_MP".equals(role) || "FUNCIONARIO_OP".equals(role);
            
            colAcoes.getChildren().add(btnDetalhes);
            if (!isFuncionario) {
                colAcoes.getChildren().addAll(btnEditar, btnComposicao, btnEliminar);
            }

            row.getChildren().addAll(lblSku, lblNome, colAcoes);
            tabelaContainer.getChildren().add(row);
        }
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

    private VBox criarEstadoVazio() {
        VBox empty = new VBox();
        empty.getStyleClass().add("estado-vazio");

        FontIcon icone = new FontIcon("mdi2p-package-variant-closed");
        icone.getStyleClass().add("estado-vazio-icone");

        Label titulo = new Label("Nenhum produto final encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        Label subtitulo = new Label("Não existem produtos finais registados.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");

        empty.getChildren().addAll(icone, titulo, subtitulo);
        return empty;
    }

    @FXML
    private void handlePaginaAnterior() {
        if (currentPage > 0) {
            currentPage--;
            carregarDados();
        }
    }

    @FXML
    private void handleProximaPagina() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            carregarDados();
        }
    }

    @FXML
    private void handleNovo() {
        javafx.stage.Window owner = btnNovo != null
                ? btnNovo.getScene().getWindow()
                : rootStack.getScene().getWindow();
        CriarProdutoFinalModalController.show(service, materiaPrimaService, owner, msg -> {
            mostrarSucesso(msg);
            carregarDados();
        });
    }

    private void setLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        }
        if (btnNovo != null) btnNovo.setDisable(loading);
        if (fab     != null) fab.setDisable(loading);
    }

    private void mostrarSucesso(String msg) {
        MessageHelper.mostrar(rootStack, msg, true);
    }

    private void mostrarErro(String msg) {
        MessageHelper.mostrar(rootStack, msg, false);
    }
}
