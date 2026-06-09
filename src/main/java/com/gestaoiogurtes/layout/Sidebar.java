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

    // ── AppAware ──────────────────────────────────────────────────
    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
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