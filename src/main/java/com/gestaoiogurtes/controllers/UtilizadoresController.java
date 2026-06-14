package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.utilizadores.*;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.models.utilizador.UserResponse;
import com.gestaoiogurtes.services.EmpresaService;
import com.gestaoiogurtes.services.UtilizadorService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.MessageHelper;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import javafx.fxml.FXML;

import java.util.List;

import java.util.function.Consumer;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.models.PaginatedResponse;

public class UtilizadoresController implements AppAware {

    private final UtilizadorService service = new UtilizadorService();
    private final EmpresaService empresaService = new EmpresaService();

    @FXML
    private Sidebar sidebarController;
    @FXML
    private StackPane rootStack;

    // Aba Ativos
    @FXML
    private VBox tabelaAtivosContainer;
    @FXML
    private VBox loadingOverlayAtivos;
    @FXML
    private ComboBox<String> cbFiltroRole;
    @FXML
    private Button btnAnteriorAtivos;
    @FXML
    private Button btnProximaAtivos;
    @FXML
    private Label lblPaginaAtivos;
    @FXML
    private ComboBox<Integer> cbTamanhoPaginaAtivos;

    // Aba Inativos
    @FXML
    private VBox tabelaInativosContainer;
    @FXML
    private VBox loadingOverlayInativos;
    @FXML
    private Button btnAnteriorInativos;
    @FXML
    private Button btnProximaInativos;
    @FXML
    private Label lblPaginaInativos;
    @FXML
    private ComboBox<Integer> cbTamanhoPaginaInativos;

    private int currentPageAtivos = 0;
    private int pageSizeAtivos = 20;
    private int totalPagesAtivos = 0;
    private List<UserResponse> todosAtivos = List.of();

    private int currentPageInativos = 0;
    private int pageSizeInativos = 20;
    private int totalPagesInativos = 0;
    private List<UserResponse> todosInativos = List.of();

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        if (cbFiltroRole != null) {
            cbFiltroRole.getItems().addAll("Todos", "Gestores", "Clientes", "Admins", "Funcionários");
            cbFiltroRole.setValue("Todos");
            cbFiltroRole.valueProperty().addListener((obs, old, val) -> {
                currentPageAtivos = 0;
                carregarAtivos();
            });
        }

        setupComboAtivos();
        setupComboInativos();

        carregarAtivos();
        carregarInativos();
    }

    private void setupComboAtivos() {
        if (cbTamanhoPaginaAtivos != null) {
            cbTamanhoPaginaAtivos.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPaginaAtivos.setValue(pageSizeAtivos);
            cbTamanhoPaginaAtivos.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSizeAtivos) {
                    pageSizeAtivos = val;
                    currentPageAtivos = 0;
                    carregarAtivos();
                }
            });
        }
    }

    private void setupComboInativos() {
        if (cbTamanhoPaginaInativos != null) {
            cbTamanhoPaginaInativos.getItems().addAll(5, 10, 20, 50, 100);
            cbTamanhoPaginaInativos.setValue(pageSizeInativos);
            cbTamanhoPaginaInativos.valueProperty().addListener((obs, old, val) -> {
                if (val != null && val != pageSizeInativos) {
                    pageSizeInativos = val;
                    currentPageInativos = 0;
                    carregarInativos();
                }
            });
        }
    }

    @FXML
    private void handleCriarGestor() {
        CriarGestorModalController.show(service,
                rootStack.getScene().getWindow(), this::onMutacaoBemSucedida);
    }

    @FXML
    private void handleCriarCliente() {
        CriarClienteModalController.show(service, empresaService,
                rootStack.getScene().getWindow(), this::onMutacaoBemSucedida);
    }

    @FXML
    private void handleCriarAdmin() {
        CriarAdminModalController.show(service,
                rootStack.getScene().getWindow(), this::onMutacaoBemSucedida);
    }

    @FXML
    private void handleCriarFuncionarioOp() {
        CriarFuncionarioOpModalController.show(service,
                rootStack.getScene().getWindow(), this::onMutacaoBemSucedida);
    }

    @FXML
    private void handleCriarFuncionarioMp() {
        CriarFuncionarioMpModalController.show(service,
                rootStack.getScene().getWindow(), this::onMutacaoBemSucedida);
    }

    private void carregarAtivos() {
        String filtro = cbFiltroRole != null ? cbFiltroRole.getValue() : "Todos";
        Consumer<QueryState<PaginatedResponse<UserResponse>>> cb = state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoadingAtivos(true);
                case SUCCESS -> {
                    setLoadingAtivos(false);
                    var response = state.getData();
                    if (response != null) {
                        todosAtivos = response.content != null ? response.content : List.of();
                        totalPagesAtivos = response.totalPages;
                        if (lblPaginaAtivos != null) {
                            lblPaginaAtivos.setText(
                                    "Página " + (currentPageAtivos + 1) + " de " + Math.max(1, totalPagesAtivos));
                        }
                        if (btnAnteriorAtivos != null)
                            btnAnteriorAtivos.setDisable(response.first);
                        if (btnProximaAtivos != null)
                            btnProximaAtivos.setDisable(response.last);
                    } else {
                        todosAtivos = List.of();
                    }
                    renderizarAtivos();
                }
                case ERROR -> {
                    setLoadingAtivos(false);
                    mostrarNotificacao("Erro ao carregar utilizadores ativos: " + state.getErrorMessage(), false);
                }
                default -> {
                }
            }
        };

        switch (filtro) {
            case "Gestores" -> service.getGestores(currentPageAtivos, pageSizeAtivos, cb);
            case "Clientes" -> service.getClientes(currentPageAtivos, pageSizeAtivos, cb);
            case "Admins" -> service.getAdmins(currentPageAtivos, pageSizeAtivos, cb);
            case "Funcionários" -> service.getFuncionarios(currentPageAtivos, pageSizeAtivos, cb);
            default -> service.getAllActive(currentPageAtivos, pageSizeAtivos, cb);
        }
    }

    private void carregarInativos() {
        service.getAllInactive(currentPageInativos, pageSizeInativos, state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoadingInativos(true);
                case SUCCESS -> {
                    setLoadingInativos(false);
                    var response = state.getData();
                    if (response != null) {
                        todosInativos = response.content != null ? response.content : List.of();
                        totalPagesInativos = response.totalPages;
                        if (lblPaginaInativos != null) {
                            lblPaginaInativos.setText(
                                    "Página " + (currentPageInativos + 1) + " de " + Math.max(1, totalPagesInativos));
                        }
                        if (btnAnteriorInativos != null)
                            btnAnteriorInativos.setDisable(response.first);
                        if (btnProximaInativos != null)
                            btnProximaInativos.setDisable(response.last);
                    } else {
                        todosInativos = List.of();
                    }
                    renderizarInativos();
                }
                case ERROR -> {
                    setLoadingInativos(false);
                    mostrarNotificacao("Erro ao carregar utilizadores inativos: " + state.getErrorMessage(), false);
                }
                default -> {
                }
            }
        });
    }

    @FXML
    private void handlePaginaAnteriorAtivos() {
        if (currentPageAtivos > 0) {
            currentPageAtivos--;
            carregarAtivos();
        }
    }

    @FXML
    private void handleProximaPaginaAtivos() {
        if (currentPageAtivos < totalPagesAtivos - 1) {
            currentPageAtivos++;
            carregarAtivos();
        }
    }

    @FXML
    private void handlePaginaAnteriorInativos() {
        if (currentPageInativos > 0) {
            currentPageInativos--;
            carregarInativos();
        }
    }

    @FXML
    private void handleProximaPaginaInativos() {
        if (currentPageInativos < totalPagesInativos - 1) {
            currentPageInativos++;
            carregarInativos();
        }
    }

    private void renderizarAtivos() {
        tabelaAtivosContainer.getChildren().clear();
        if (todosAtivos.isEmpty()) {
            tabelaAtivosContainer.getChildren().add(criarEstadoVazio("Nenhum utilizador ativo encontrado"));
            return;
        }
        tabelaAtivosContainer.getChildren().add(criarLinhaHeader(true));
        for (int i = 0; i < todosAtivos.size(); i++) {
            var linha = criarLinhaAtivo(todosAtivos.get(i), i);
            if (i == todosAtivos.size() - 1)
                linha.getStyleClass().add("tabela-linha-ultima");
            tabelaAtivosContainer.getChildren().add(linha);
        }
    }

    private void renderizarInativos() {
        tabelaInativosContainer.getChildren().clear();
        if (todosInativos.isEmpty()) {
            tabelaInativosContainer.getChildren().add(criarEstadoVazio("Nenhum utilizador inativo encontrado"));
            return;
        }
        tabelaInativosContainer.getChildren().add(criarLinhaHeader(false));
        for (int i = 0; i < todosInativos.size(); i++) {
            var linha = criarLinhaInativo(todosInativos.get(i), i);
            if (i == todosInativos.size() - 1)
                linha.getStyleClass().add("tabela-linha-ultima");
            tabelaInativosContainer.getChildren().add(linha);
        }
    }

    private HBox criarLinhaHeader(boolean comBotoesEdicao) {
        var row = new HBox();
        row.getStyleClass().add("tabela-header");
        row.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().addAll(
                headerCol("", 48, false),
                headerCol("Nome", 220, true),
                headerCol("Email", 200, false),
                headerCol("Role", 130, false),
                headerCol("Ações", comBotoesEdicao ? 220 : 110, false));
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

    private HBox criarLinhaAtivo(UserResponse u, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Nome
        var nomeLabel = new Label(u.nome != null ? u.nome : "—");
        nomeLabel.getStyleClass().add("celula-nome-principal");
        nomeLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeLabel, Priority.ALWAYS);

        // Email
        var emailLabel = new Label(u.email != null ? u.email : "—");
        emailLabel.getStyleClass().add("celula-dados");
        emailLabel.setMinWidth(200);

        // Role pill
        var pill = criarRolePill(u.role);

        // Botões
        var btnDetalhes = new Button("Detalhes");
        var btnEditar = new Button("Editar");
        var btnDesativar = new Button("Desativar");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnDesativar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnDetalhes.setOnAction(e -> abrirDetalhes(u, false));
        btnEditar.setOnAction(e -> abrirEditar(u));
        btnDesativar.setOnAction(e -> DesativarUtilizadorModalController.show(
                u, service,
                rootStack.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        var acoesBox = new HBox(6, btnDetalhes, btnEditar, btnDesativar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(220);

        var avatar = criarAvatar(u.nome);
        row.getChildren().addAll(avatar, nomeLabel, emailLabel, pill, acoesBox);
        return row;
    }

    private HBox criarLinhaInativo(UserResponse u, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        var nomeLabel = new Label(u.nome != null ? u.nome : "—");
        nomeLabel.getStyleClass().add("celula-nome-principal");
        nomeLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeLabel, Priority.ALWAYS);

        var emailLabel = new Label(u.email != null ? u.email : "—");
        emailLabel.getStyleClass().add("celula-dados");
        emailLabel.setMinWidth(200);

        var pill = criarRolePill(u.role);

        var btnDetalhes = new Button("Detalhes");
        btnDetalhes.getStyleClass().add("btn-linha-acao");
        btnDetalhes.setOnAction(e -> abrirDetalhes(u, true));

        /*
         * TODO: Reativar — endpoint not yet available
         * var btnReativar = new Button("Reativar");
         * btnReativar.getStyleClass().add("btn-linha-acao");
         * btnReativar.setOnAction(e -> {
         * // implementar reativação
         * });
         */

        var acoesBox = new HBox(6, btnDetalhes);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(110);

        var avatar = criarAvatar(u.nome);
        row.getChildren().addAll(avatar, nomeLabel, emailLabel, pill, acoesBox);
        return row;
    }

    private Label criarRolePill(String role) {
        var pill = new Label(roleLegivel(role));
        pill.getStyleClass().addAll("role-pill", pilStyleClass(role));
        pill.setMinWidth(130);
        return pill;
    }

    private String roleLegivel(String role) {
        if (role == null)
            return "—";
        return switch (role) {
            case "ADMIN" -> "Admin";
            case "GESTOR" -> "Gestor";
            case "CLIENTE" -> "Cliente";
            case "FUNCIONARIO_OP" -> "Funcionário OP";
            case "FUNCIONARIO_MP" -> "Funcionário MP";
            default -> role;
        };
    }

    private String pilStyleClass(String role) {
        if (role == null)
            return "pill-desconhecido";
        return switch (role) {
            case "ADMIN" -> "pill-admin";
            case "GESTOR" -> "pill-gestor";
            case "CLIENTE" -> "pill-cliente";
            case "FUNCIONARIO_OP" -> "pill-funcionario-op";
            case "FUNCIONARIO_MP" -> "pill-funcionario-mp";
            default -> "pill-desconhecido";
        };
    }

    private void abrirDetalhes(UserResponse u, boolean inativo) {
        var window = rootStack.getScene().getWindow();
        if (u.role == null)
            return;
        switch (u.role) {
            case "GESTOR" -> DetalhesGestorModalController.show(u, inativo, window);
            case "CLIENTE" -> DetalhesClienteModalController.show(u, inativo, window, empresaService);
            case "ADMIN" -> DetalhesAdminModalController.show(u, inativo, window);
            case "FUNCIONARIO_OP" -> DetalhesFuncionarioOpModalController.show(u, inativo, window);
            case "FUNCIONARIO_MP" -> DetalhesFuncionarioMpModalController.show(u, inativo, window);
        }
    }

    private void abrirEditar(UserResponse u) {
        var window = rootStack.getScene().getWindow();
        if (u.role == null)
            return;
        switch (u.role) {
            case "GESTOR" -> EditarGestorModalController.show(u, service, window, this::onMutacaoBemSucedida);
            case "CLIENTE" ->
                EditarClienteModalController.show(u, service, empresaService, window, this::onMutacaoBemSucedida);
            case "ADMIN" -> EditarAdminModalController.show(u, service, window, this::onMutacaoBemSucedida);
            case "FUNCIONARIO_OP",
                    "FUNCIONARIO_MP" ->
                EditarFuncionarioModalController.show(u, service, window, this::onMutacaoBemSucedida);
        }
    }

    private VBox criarEstadoVazio(String mensagem) {
        var icone = new FontIcon(MaterialDesignA.ACCOUNT_GROUP);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label(mensagem);
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou usa o botão \"Criar Utilizador\" para adicionar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    private void setLoadingAtivos(boolean loading) {
        if (loadingOverlayAtivos != null) {
            loadingOverlayAtivos.setVisible(loading);
            loadingOverlayAtivos.setManaged(loading);
        }
    }

    private void setLoadingInativos(boolean loading) {
        if (loadingOverlayInativos != null) {
            loadingOverlayInativos.setVisible(loading);
            loadingOverlayInativos.setManaged(loading);
        }
    }

    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }

    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarAtivos();
        carregarInativos();
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
        if (nome == null || nome.isBlank())
            return "?";
        var partes = nome.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return (partes[0].charAt(0) + "" + partes[partes.length - 1].charAt(0)).toUpperCase();
    }

}
