package com.gestaoiogurtes.components.materiaPrima;

import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaFornecedorResponse;
import com.gestaoiogurtes.services.MateriaPrimaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

/**
 * Mini-CRUD de fornecedores de uma matéria prima.
 * Abre-se via {@link #show(String, String, MateriaPrimaService, Window)}.
 */
public class FornecedoresMateriaPrimaModalController {

    @FXML private Label  lblTitulo;
    @FXML private Label  lblPagina;
    @FXML private Button btnAnterior;
    @FXML private Button btnProximo;
    @FXML private VBox   listaContainer;
    @FXML private VBox   loadingOverlay;
    @FXML private Button btnAdicionarFornecedor;

    private Stage              dialogStage;
    private MateriaPrimaService service;
    private String             materiaId;
    private String             materiaNome;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 10;

    // ── Abertura ────────────────────────────────────────────────────────────

    public static void show(String materiaId, String materiaNome,
                            MateriaPrimaService service, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FornecedoresMateriaPrimaModalController.class
                            .getResource("/fxml/components/materiaPrima/FornecedoresMateriaPrimaModal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(true);
            stage.setTitle("Fornecedores – " + materiaNome);
            stage.setScene(new Scene(root));

            FornecedoresMateriaPrimaModalController ctrl = loader.getController();
            ctrl.dialogStage  = stage;
            ctrl.service      = service;
            ctrl.materiaId    = materiaId;
            ctrl.materiaNome  = materiaNome;
            ctrl.lblTitulo.setText("Fornecedores de " + materiaNome);

            ctrl.carregarFornecedores(0);

            String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();
            if ("FUNCIONARIO_MP".equals(role) || "FUNCIONARIO_OP".equals(role)) {
                if (ctrl.btnAdicionarFornecedor != null) {
                    ctrl.btnAdicionarFornecedor.setVisible(false);
                    ctrl.btnAdicionarFornecedor.setManaged(false);
                }
            }

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── Handlers FXML ───────────────────────────────────────────────────────

    @FXML
    private void handleAdicionarFornecedor() {
        AdicionarFornecedorMateriaPrimaModalController.show(
                materiaId, service, dialogStage,
                () -> carregarFornecedores(currentPage));
    }

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) carregarFornecedores(currentPage - 1);
    }

    @FXML
    private void handleProximo() {
        if (currentPage < totalPages - 1) carregarFornecedores(currentPage + 1);
    }

    @FXML
    private void handleFechar() {
        dialogStage.close();
    }

    // ── Carregamento ────────────────────────────────────────────────────────

    void carregarFornecedores(int page) {
        setLoading(true);

        service.getFornecedores(materiaId, page, PAGE_SIZE, state -> {
            if (state.isLoading()) {
                setLoading(true);
            } else if (state.isSuccess()) {
                setLoading(false);
                var resposta = state.getData();
                if (resposta != null) {
                    currentPage = page;
                    totalPages  = Math.max(1, resposta.totalPages);
                    lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
                    btnAnterior.setDisable(resposta.first);
                    btnProximo.setDisable(resposta.last);
                    renderizarLista(resposta.content != null ? resposta.content : List.of());
                }
            } else if (state.isError()) {
                setLoading(false);
                listaContainer.getChildren().clear();
                var lbl = new Label("Erro ao carregar: " + state.getErrorMessage());
                lbl.getStyleClass().add("cert-erro-label");
                listaContainer.getChildren().add(lbl);
            }
        });
    }

    // ── Renderização ────────────────────────────────────────────────────────

    private void renderizarLista(List<MateriaPrimaFornecedorResponse> items) {
        listaContainer.getChildren().clear();

        if (items.isEmpty()) {
            var lbl = new Label("Nenhum fornecedor associado.");
            lbl.getStyleClass().add("cert-vazio-label");
            listaContainer.getChildren().add(lbl);
            return;
        }

        // Header
        var header = new HBox();
        header.getStyleClass().add("tabela-header");
        header.getChildren().addAll(
                headerCol("Fornecedor", true),
                headerCol("Preço Unitário", false),
                headerCol("Preferencial", false));
                
        String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();
        if (!"FUNCIONARIO_MP".equals(role) && !"FUNCIONARIO_OP".equals(role)) {
            header.getChildren().add(headerCol("Ações", false));
        }
        listaContainer.getChildren().add(header);

        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var row  = criarLinha(item);
            if (i == items.size() - 1) row.getStyleClass().add("tabela-linha-ultima");
            listaContainer.getChildren().add(row);
        }
    }

    private Label headerCol(String texto, boolean grow) {
        var lbl = new Label(texto.toUpperCase());
        lbl.getStyleClass().add("tabela-header-label");
        if (grow) {
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
        } else {
            lbl.setMinWidth(130);
        }
        return lbl;
    }

    private HBox criarLinha(MateriaPrimaFornecedorResponse item) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);

        var lblNome = new Label(item.fornecedorNome != null ? item.fornecedorNome : "—");
        lblNome.getStyleClass().add("celula-nome-principal");
        lblNome.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblNome, Priority.ALWAYS);

        String simbolo = item.moedaSimbolo != null ? item.moedaSimbolo : "";
        String preco   = item.precoUnitario != null ? String.format("%.2f %s", item.precoUnitario, simbolo) : "—";
        var lblPreco = new Label(preco);
        lblPreco.getStyleClass().add("celula-dados");
        lblPreco.setMinWidth(130);

        String prefStr = Boolean.TRUE.equals(item.preferencial) ? "Sim" : "Não";
        var lblPref = new Label(prefStr);
        lblPref.getStyleClass().add("celula-dados");
        lblPref.setMinWidth(130);

        var btnEditar   = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> EditarFornecedorMateriaPrimaModalController.show(
                item, service, dialogStage,
                () -> carregarFornecedores(currentPage)));

        btnEliminar.setOnAction(e -> EliminarFornecedorMateriaPrimaModalController.show(
                item.id != null ? item.id.toString() : null,
                item.fornecedorNome,
                service, dialogStage,
                () -> carregarFornecedores(currentPage)));

        var acoes = new HBox(6, btnEditar, btnEliminar);
        acoes.setMinWidth(140);
        acoes.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(lblNome, lblPreco, lblPref);
        String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();
        if (!"FUNCIONARIO_MP".equals(role) && !"FUNCIONARIO_OP".equals(role)) {
            row.getChildren().add(acoes);
        }
        return row;
    }

    // ── Helpers de UI ───────────────────────────────────────────────────────

    private void setLoading(boolean loading) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setManaged(loading);
        btnAdicionarFornecedor.setDisable(loading);
    }
}
