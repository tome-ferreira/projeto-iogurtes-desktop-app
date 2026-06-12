package com.gestaoiogurtes.layout;

import atlantafx.base.theme.*;
import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.NavigationHelper;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

/**
 * Controller for Sidebar.fxml.
 *
 * <p>Static structure (icons, labels, separators) lives in the FXML.
 * This class keeps all behavioural logic:
 * <ul>
 *   <li>Expand / collapse toggle</li>
 *   <li>Theme cycling</li>
 *   <li>Navigation to other pages</li>
 * </ul>
 *
 * <p>The {@link GestaoIogurtes} reference must be injected after FXML loading
 * via {@link #setApp(GestaoIogurtes)}, which is handled automatically by
 * {@link NavigationHelper} for pages that implement {@link AppAware}.
 */
public class Sidebar implements AppAware {

    // ── Constants ─────────────────────────────────────────────────
    private static final double LARGURA_EXPANDIDA = 240;
    private static final double LARGURA_RECOLHIDA = 58;

    private final String[] temas = {
            new PrimerLight().getUserAgentStylesheet(),
            new PrimerDark().getUserAgentStylesheet(),
            new NordLight().getUserAgentStylesheet(),
            new NordDark().getUserAgentStylesheet(),
            new CupertinoLight().getUserAgentStylesheet(),
            new CupertinoDark().getUserAgentStylesheet(),
            new Dracula().getUserAgentStylesheet()
    };
    private final String[] nomesTemas = {
            "Primer Light", "Primer Dark", "Nord Light",
            "Nord Dark", "Cupertino Light", "Cupertino Dark", "Dracula"
    };
    private int temaAtual = 0;
    private boolean expandida = true;

    // ── App reference — injected after FXML load ───────────────────
    private GestaoIogurtes app;

    // ── FXML references ───────────────────────────────────────────
    @FXML private VBox root;
    @FXML private Label tituloLabel;
    @FXML private Button btnToggle;
    @FXML private Tooltip temaTooltip;

    // Sections
    @FXML private Label lblSectionDashboards;
    @FXML private Label lblSectionProdutos;
    @FXML private Label lblSectionEncomendas;
    @FXML private Label lblSectionMateriasPrimas;
    @FXML private Label lblSectionFornecedores;
    @FXML private Label lblSectionGestao;

    // Dashboards
    @FXML private Button btnDashboard;
    @FXML private Button btnDashboardAdmin;
    @FXML private Button btnDashboardGestor;
    @FXML private Button btnDashboardMp;
    @FXML private Button btnDashboardOp;

    // Produtos
    @FXML private Button btnStock;
    @FXML private Button btnProdutosFinais;
    @FXML private Button btnOrdensProducao;

    // Encomendas
    @FXML private Button btnEncomendas;

    // Materias Primas
    @FXML private Button btnMateriasPrimas;
    @FXML private Button btnEncomendasMp;
    @FXML private Button btnTiposMateriaPrima;

    // Fornecedores
    @FXML private Button btnFornecedores;
    @FXML private Button btnCertificacoes;
    @FXML private Button btnTiposFornecedor;

    // Gestao
    @FXML private Button btnUtilizadores;
    @FXML private Button btnEmpresas;
    @FXML private Button btnTiposPallet;
    @FXML private Button btnMoedas;

    // ── AppAware ──────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
    }

    // ── RBAC Visibility ───────────────────────────────────────────

    @FXML
    public void initialize() {
        applyRoleBasedVisibility();
    }

    private void hideNode(Node... nodes) {
        for (Node n : nodes) {
            if (n != null) {
                n.setVisible(false);
                n.setManaged(false);
            }
        }
    }

    private void applyRoleBasedVisibility() {
        String role = com.gestaoiogurtes.utils.SessionManager.getInstance().getUserRole();

        // Sempre esconder o dashboard base para todos, uma vez que cada role tem o seu próprio
        hideNode(btnDashboard);

        // Ocultar os dashboards de outros roles por defeito
        hideNode(btnDashboardAdmin, btnDashboardGestor, btnDashboardMp, btnDashboardOp);

        if ("ADMIN".equals(role)) {
            btnDashboardAdmin.setVisible(true);
            btnDashboardAdmin.setManaged(true);
            // Vê tudo o resto

        } else if ("GESTOR".equals(role)) {
            btnDashboardGestor.setVisible(true);
            btnDashboardGestor.setManaged(true);

            // Esconde Utilizadores e Empresas
            hideNode(btnUtilizadores, btnEmpresas);

        } else if ("FUNCIONARIO_MP".equals(role)) {
            btnDashboardMp.setVisible(true);
            btnDashboardMp.setManaged(true);

            // Vê Fornecedores, Matérias Primas, Produtos Finais, Encomenda M.P. e Stock.
            hideNode(
                btnOrdensProducao,
                lblSectionEncomendas, btnEncomendas,
                btnTiposMateriaPrima,
                btnCertificacoes, btnTiposFornecedor,
                lblSectionGestao, btnUtilizadores, btnEmpresas, btnTiposPallet, btnMoedas
            );

        } else if ("FUNCIONARIO_OP".equals(role)) {
            btnDashboardOp.setVisible(true);
            btnDashboardOp.setManaged(true);

            // Vê Matérias primas, Ordens de produção, Produtos Finais e Stock.
            hideNode(
                lblSectionEncomendas, btnEncomendas,
                btnEncomendasMp, btnTiposMateriaPrima,
                lblSectionFornecedores, btnFornecedores, btnCertificacoes, btnTiposFornecedor,
                lblSectionGestao, btnUtilizadores, btnEmpresas, btnTiposPallet, btnMoedas
            );
        }
    }

    // ── FXML event handlers ───────────────────────────────────────

    @FXML
    private void handleDashboard() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
    }

    @FXML
    private void handleDashboardAdmin() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardAdmin.fxml");
    }

    @FXML
    private void handleDashboardGestor() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardGestor.fxml");
    }

    @FXML
    private void handleDashboardMp() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioMp.fxml");
    }

    @FXML
    private void handleDashboardOp() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioOp.fxml");
    }

    @FXML
    private void handleEmpresas() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Empresas.fxml");
    }

    @FXML
    private void handleEncomendas() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Encomenda.fxml");
    }

    @FXML
    private void handleEncomendasMp() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/EncomendaMp.fxml");
    }

    @FXML
    private void handleOrdensProducao() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/OrdensProducao.fxml");
    }

    @FXML
    private void handleFornecedores() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Fornecedores.fxml");
    }

    @FXML
    private void handleMateriasPrimas() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/MateriasPrimas.fxml");
    }

    @FXML
    private void handleMoedas() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Moedas.fxml");
    }

    @FXML
    private void handleUtilizadores() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Utilizadores.fxml");
    }

    @FXML
    private void handleTiposFornecedor() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/FornecedoresTipo.fxml");
    }

    @FXML
    private void handleCertificacoes() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Certificacoes.fxml");
    }

    @FXML
    private void handleTiposMateriaPrima() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/TiposMateriaPrima.fxml");
    }

    @FXML
    private void handleTiposPallet() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/TiposPallet.fxml");
    }

    @FXML
    private void handleProdutosFinais() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/ProdutosFinais.fxml");
    }

    @FXML
    private void handleStock() {
        NavigationHelper.navigateTo(app, "/fxml/paginas/Stock.fxml");
    }

    @FXML
    private void handleTema() {
        temaAtual = (temaAtual + 1) % temas.length;
        Application.setUserAgentStylesheet(temas[temaAtual]);
        temaTooltip.setText(nomesTemas[temaAtual]);
    }

    @FXML
    private void handleSair() {
        com.gestaoiogurtes.utils.SessionManager.getInstance().clearSession();
        NavigationHelper.navigateTo(app, "/fxml/paginas/PaginaLogin.fxml");
    }

    @FXML
    private void handleToggle() {
        toggleSidebar();
    }

    // ── Toggle logic ──────────────────────────────────────────────

    private void toggleSidebar() {
        expandida = !expandida;

        btnToggle.setGraphic(new FontIcon(
                expandida ? MaterialDesignC.CHEVRON_LEFT : MaterialDesignC.CHEVRON_RIGHT));

        root.setPrefWidth(expandida ? LARGURA_EXPANDIDA : LARGURA_RECOLHIDA);

        tituloLabel.setVisible(expandida);
        tituloLabel.setManaged(expandida);

        setLabelsVisiveis(expandida);
    }

    private void setLabelsVisiveis(boolean visivel) {
        root.getChildren().forEach(node -> esconderLabelsEm(node, visivel));
    }

    private void esconderLabelsEm(Node node, boolean visivel) {
        if (node instanceof VBox vbox) {
            vbox.getChildren().forEach(child -> esconderLabelsEm(child, visivel));
        }
        if (node instanceof Button btn && btn.getGraphic() instanceof HBox hbox) {
            hbox.getChildren().forEach(child -> {
                if (child instanceof Label lbl) {
                    lbl.setVisible(visivel);
                    lbl.setManaged(visivel);
                }
            });
        }
        // Section labels ("MENU") have the text-muted style class
        if (node instanceof Label lbl && lbl.getStyleClass().contains("text-muted")) {
            lbl.setVisible(visivel);
            lbl.setManaged(visivel);
        }
    }
}