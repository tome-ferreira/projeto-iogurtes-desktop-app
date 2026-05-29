package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.utils.AppAware;

import com.gestaoiogurtes.components.materiaPrima.CriarMateriaPrimaModalController;
import com.gestaoiogurtes.components.materiaPrima.DetalhesMateriaPrimaModalController;
import com.gestaoiogurtes.components.materiaPrima.EditarMateriaPrimaModalController;
import com.gestaoiogurtes.components.materiaPrima.EliminarMateriaPrimaModalController;
import com.gestaoiogurtes.components.materiaPrima.FornecedoresMateriaPrimaModalController;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import com.gestaoiogurtes.services.TipoMateriaPrimaService;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import javafx.stage.Window;

public class MateriasPrimasController implements AppAware {

    @FXML private StackPane rootStack;
    @FXML private VBox tabelaContainer;
    @FXML private VBox loadingOverlay;
    @FXML private Button btnNovo;
    @FXML private Button fab;
    
    @FXML private Label lblPagina;
    @FXML private Button btnAnterior;
    @FXML private Button btnProxima;
    @FXML private ComboBox<Integer> cbTamanhoPagina;

    @FXML private com.gestaoiogurtes.layout.Sidebar sidebarController;

    private MateriaPrimaService service;
    private TipoMateriaPrimaService tipoService;

    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;

    @Override
    public void setApp(com.gestaoiogurtes.GestaoIogurtes app) {
        if (sidebarController != null) {
            sidebarController.setApp(app);
        }
    }

    public void initialize() {
        service = new MateriaPrimaService();
        tipoService = new TipoMateriaPrimaService();

        if (cbTamanhoPagina != null) {
            cbTamanhoPagina.getItems().addAll(10, 20, 50, 100);
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
                    if (btnProxima != null) btnProxima.setDisable(response.last);

                    renderizarTabela(response.content != null ? response.content : List.of());
                }
            } else if (state.isError()) {
                setLoading(false);
                mostrarErro("Erro ao carregar matérias primas: " + state.getErrorMessage());
            }
        });
    }

    private void renderizarTabela(List<MateriaPrimaResponse> itens) {
        if (itens.isEmpty()) {
            tabelaContainer.getChildren().add(criarEstadoVazio());
            return;
        }

        HBox header = new HBox();
        header.getStyleClass().add("tabela-header");
        header.setMaxWidth(Double.MAX_VALUE);

        header.getChildren().addAll(
                headerCol("NOME", 220, true),
                headerCol("TIPO", 150, false),
                headerCol("STOCK / MÍNIMO", 150, false),
                headerCol("AÇÕES", 320, false)
        );
        tabelaContainer.getChildren().add(header);

        for (int i = 0; i < itens.size(); i++) {
            MateriaPrimaResponse p = itens.get(i);
            
            HBox row = new HBox();
            row.getStyleClass().add("tabela-linha");
            if (i == itens.size() - 1) {
                row.getStyleClass().add("tabela-linha-ultima");
            }
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);

            // Coluna Nome
            Label lblNome = new Label(p.nome != null ? p.nome : "—");
            lblNome.getStyleClass().add("celula-nome-principal");
            lblNome.setMinWidth(220);
            lblNome.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lblNome, Priority.ALWAYS);
            
            // Coluna Tipo
            Label lblTipo = new Label(p.tipo != null && p.tipo.nome != null ? p.tipo.nome : "Sem tipo");
            lblTipo.getStyleClass().add("pill-materia-prima-tipo");
            HBox boxTipo = new HBox(lblTipo);
            boxTipo.setAlignment(Pos.CENTER_LEFT);
            boxTipo.setMinWidth(150);

            // Coluna Stock / Mínimo
            VBox colStock = new VBox(2);
            colStock.setMinWidth(150);
            Label lblStockAtual = new Label(p.stockAtual != null ? String.valueOf(p.stockAtual) + " " + p.unidade : "—");
            lblStockAtual.getStyleClass().add("celula-dados");
            
            Label lblStockMinimo = new Label("Mín: " + (p.stockMinimo != null ? String.valueOf(p.stockMinimo) : "0"));
            lblStockMinimo.getStyleClass().add("celula-nome-subtitulo");
            
            colStock.getChildren().addAll(lblStockAtual, lblStockMinimo);

            // Coluna Ações
            HBox colAcoes = new HBox(6);
            colAcoes.setMinWidth(320);
            colAcoes.setAlignment(Pos.CENTER_RIGHT);

            Button btnDetalhes = new Button("Detalhes");
            btnDetalhes.getStyleClass().add("btn-linha-acao");
            btnDetalhes.setOnAction(e -> DetalhesMateriaPrimaModalController.show(p, service, btnDetalhes.getScene().getWindow()));

            Button btnEditar = new Button("Editar");
            btnEditar.getStyleClass().add("btn-linha-acao");
            btnEditar.setOnAction(e -> EditarMateriaPrimaModalController.show(p, service, tipoService, btnEditar.getScene().getWindow(), msg -> {
                mostrarSucesso(msg);
                carregarDados();
            }));

            Button btnFornecedores = new Button("Fornecedores");
            btnFornecedores.getStyleClass().add("btn-linha-acao");
            btnFornecedores.setOnAction(e -> {
                String materiaId   = p.id != null ? p.id.toString() : null;
                String materiaNome = p.nome != null ? p.nome : "Matéria Prima";
                FornecedoresMateriaPrimaModalController.show(materiaId, materiaNome, service, btnFornecedores.getScene().getWindow());
            });

            Button btnEliminar = new Button("Eliminar");
            btnEliminar.getStyleClass().add("btn-linha-danger");
            btnEliminar.setOnAction(e -> EliminarMateriaPrimaModalController.show(p, service, btnEliminar.getScene().getWindow(), msg -> {
                mostrarSucesso(msg);
                carregarDados();
            }));

            colAcoes.getChildren().addAll(btnDetalhes, btnEditar, btnFornecedores, btnEliminar);

            row.getChildren().addAll(lblNome, boxTipo, colStock, colAcoes);
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
        
        FontIcon icone = new FontIcon("mdi2i-inbox-outline");
        icone.getStyleClass().add("estado-vazio-icone");

        Label titulo = new Label("Nenhuma matéria prima encontrada");
        titulo.getStyleClass().add("estado-vazio-titulo");

        Label subtitulo = new Label("Não existem matérias primas registadas ou a pesquisa não devolveu resultados.");
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
        Window owner = btnNovo != null ? btnNovo.getScene().getWindow() : rootStack.getScene().getWindow();
        CriarMateriaPrimaModalController.show(service, tipoService, owner, msg -> {
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
        if (fab != null) fab.setDisable(loading);
    }

    private void mostrarSucesso(String msg) {
        MessageHelper.mostrar(rootStack, msg, true);
    }

    private void mostrarErro(String msg) {
        MessageHelper.mostrar(rootStack, msg, false);
    }
}
