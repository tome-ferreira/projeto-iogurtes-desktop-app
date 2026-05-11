package com.gestaoiogurtes.paginas;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.components.empresas.CriarEmpresaModalController;
import com.gestaoiogurtes.components.empresas.EditarEmpresaModalController;
import com.gestaoiogurtes.components.empresas.EliminarEmpresaModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.model.EmpresaResponse;
import com.gestaoiogurtes.services.real.RealEmpresaService;
import com.gestaoiogurtes.services.interfaces.IEmpresaApiService;
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


/**
 * Controller para Empresas.fxml.
 *
 * <p><strong>Padrão de carregamento de dados:</strong></p>
 * <ol>
 *   <li>Toda a chamada HTTP é feita via {@link RealEmpresaService} +
 *       {@code ApiQuery.execute()} (já encapsulado no serviço).</li>
 *   <li>No estado {@code LOADING}: o overlay de loading é mostrado e os botões
 *       de acção são desactivados.</li>
 *   <li>No estado {@code SUCCESS}: o overlay é escondido, a tabela é actualizada.</li>
 *   <li>No estado {@code ERROR}: o overlay é escondido, uma notificação de erro
 *       é apresentada no topo direito.</li>
 * </ol>
 *
 * <p><strong>Regra fundamental:</strong> {@code Platform.runLater()} nunca é chamado aqui.
 * O {@link com.gestaoiogurtes.api.ApiQuery} já garante que os callbacks chegam
 * na JavaFX Application Thread.</p>
 */
public class EmpresasController implements AppAware {

    // ── Serviço ───────────────────────────────────────────────────────────────
    private final IEmpresaApiService service = new RealEmpresaService();

    // ── FXML references ───────────────────────────────────────────────────────
    @FXML private Sidebar    sidebarController;
    @FXML private VBox       tabelaContainer;
    @FXML private StackPane  rootStack;
    @FXML private VBox       loadingOverlay;
    @FXML private TextField  campoPesquisa;
    @FXML private Button     btnNovo;
    @FXML private Button     fab;

    // ── Estado local ──────────────────────────────────────────────────────────
    /** Cache da última lista recebida da API, usada pelos filtros. */
    private List<EmpresaResponse> todasEmpresas = List.of();

    // ── AppAware ──────────────────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela(val.trim().toLowerCase()));
        carregarEmpresas();
    }

    // ── FXML handlers ─────────────────────────────────────────────────────────

    @FXML
    private void handleNovo() {
        CriarEmpresaModalController.show(
                service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida
        );
    }

    // ── Carregamento de dados ─────────────────────────────────────────────────

    /**
     * Dispara o pedido GET /empresas via ApiQuery.
     * Aplica loading overlay durante o pedido e actualiza a tabela no sucesso.
     */
    private void carregarEmpresas() {
        service.getAll(state -> {
            switch (state.getStatus()) {
                case LOADING -> setLoading(true);

                case SUCCESS -> {
                    setLoading(false);
                    todasEmpresas = state.getData() != null ? state.getData() : List.of();
                    filtrarTabela(campoPesquisa != null ? campoPesquisa.getText().toLowerCase().trim() : "");
                }

                case ERROR -> {
                    setLoading(false);
                    mostrarNotificacao("Erro ao carregar empresas: " + state.getErrorMessage(), false);
                }

                default -> {} // IDLE — ignorar
            }
        });
    }

    // ── Filtros ───────────────────────────────────────────────────────────────

    private void filtrarTabela(String pesquisa) {
        tabelaContainer.getChildren().clear();

        var filtrados = todasEmpresas.stream()
                .filter(e -> pesquisa.isEmpty()
                        || e.nomeEmpresa.toLowerCase().contains(pesquisa)
                        || e.nipc.toLowerCase().contains(pesquisa)
                        || e.cidade.toLowerCase().contains(pesquisa))
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
                headerCol("",            48,  false),
                headerCol("Empresa",    220, true),
                headerCol("NIPC",        120, false),
                headerCol("Cidade",     120, false),
                headerCol("Telefone",   120, false),
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

    // ── Linhas de dados ───────────────────────────────────────────────────────

    private HBox criarLinhaTabela(EmpresaResponse empresa, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Avatar com iniciais
        var avatar = criarAvatar(empresa.nomeEmpresa, index);

        // Nome + NIPC
        var nomeLabel = new Label(empresa.nomeEmpresa);
        nomeLabel.getStyleClass().add("celula-nome-principal");

        var nipcSubLabel = new Label("NIPC: " + empresa.nipc);
        nipcSubLabel.getStyleClass().add("celula-nome-subtitulo");

        var nomeBox = new VBox(2, nomeLabel, nipcSubLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        // NIPC (coluna separada)
        var nipcLabel = new Label(empresa.nipc);
        nipcLabel.getStyleClass().add("celula-dados");
        nipcLabel.setMinWidth(120);

        // Cidade
        var cidadeLabel = new Label(empresa.cidade != null ? empresa.cidade : "—");
        cidadeLabel.getStyleClass().add("celula-dados");
        cidadeLabel.setMinWidth(120);

        // Telefone
        var telefoneLabel = new Label(empresa.telefone != null ? empresa.telefone : "—");
        telefoneLabel.getStyleClass().add("celula-dados");
        telefoneLabel.setMinWidth(120);

        // Botões de acção
        var btnEditar   = new Button("Editar");
        var btnEliminar = new Button("Eliminar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEliminar.getStyleClass().addAll("btn-linha-acao", "btn-linha-danger");

        btnEditar.setOnAction(e -> EditarEmpresaModalController.show(
                empresa, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        btnEliminar.setOnAction(e -> EliminarEmpresaModalController.show(
                empresa, service,
                tabelaContainer.getScene().getWindow(),
                this::onMutacaoBemSucedida));

        var acoesBox = new HBox(6, btnEditar, btnEliminar);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(140);

        row.getChildren().addAll(avatar, nomeBox, nipcLabel, cidadeLabel, telefoneLabel, acoesBox);
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

    // ── Estado vazio ──────────────────────────────────────────────────────────

    private VBox criarEstadoVazio() {
        var icone = new FontIcon(MaterialDesignD.DOMAIN_OFF);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhuma empresa encontrada");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Ajusta os filtros ou clica em \"Nova empresa\" para adicionar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }

    // ── Loading overlay ───────────────────────────────────────────────────────

    /**
     * Mostra ou esconde o overlay de loading e desactiva/activa os botões de acção.
     *
     * <p>Padrão reutilizável: em cada controller CRUD, mantém um {@code VBox}
     * com styleClass {@code loading-overlay} como segundo filho do {@code StackPane}
     * raiz. Basta chamar {@code setLoading(true/false)} em LOADING/SUCCESS/ERROR.</p>
     *
     * @param loading {@code true} para mostrar; {@code false} para esconder
     */
    private void setLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        }
        if (btnNovo != null) btnNovo.setDisable(loading);
        if (fab     != null) fab.setDisable(loading);
    }

    // ── Mensagens de feedback ─────────────────────────────────────────────────

    /**
     * Apresenta uma mensagem AtlantaFX no topo direito do rootStack.
     * Delega para {@link MessageHelper#mostrar}.
     *
     * @param mensagem texto da mensagem
     * @param sucesso  {@code true} → estilo sucesso; {@code false} → estilo erro
     */
    private void mostrarNotificacao(String mensagem, boolean sucesso) {
        MessageHelper.mostrar(rootStack, mensagem, sucesso);
    }


    // ── Callback após mutação (criar / editar / eliminar) ─────────────────────

    /**
     * Chamado pelos modais após uma operação bem-sucedida.
     *
     * @param mensagem mensagem de sucesso a apresentar ao utilizador
     */
    public void onMutacaoBemSucedida(String mensagem) {
        mostrarNotificacao(mensagem, true);
        carregarEmpresas();
    }

    /**
     * Chamado pelos modais após uma operação com erro.
     *
     * @param mensagem mensagem de erro a apresentar ao utilizador
     */
    public void onMutacaoComErro(String mensagem) {
        mostrarNotificacao(mensagem, false);
    }
}
