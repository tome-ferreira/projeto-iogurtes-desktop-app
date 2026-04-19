package com.gestaoiogurtes.paginas;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.bll.model.User;
import com.gestaoiogurtes.components.utilizadores.CriarUtilizadorModalController;
import com.gestaoiogurtes.components.utilizadores.DesativarUtilizadorModalController;
import com.gestaoiogurtes.components.utilizadores.EditarUtilizadorModalController;
import com.gestaoiogurtes.layout.Sidebar;
import com.gestaoiogurtes.services.ServiceLocator;
import com.gestaoiogurtes.services.interfaces.IUserService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.EnumDisplayHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;

import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.gestaoiogurtes.bll.model.enums.UserRoleType;

public class UtilizadoresController implements AppAware {

    private final IUserService userService = ServiceLocator.userService();

    @FXML private Sidebar sidebarController;
    @FXML private VBox tabelaContainer;
    @FXML private TextField campoPesquisa;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<String> comboRoleFiltro;

    @Override
    public void setApp(GestaoIogurtes app) {
        sidebarController.setApp(app);
    }

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList(
                "Todos os estados", "Ativos", "Inativos"));
        comboEstado.setValue("Todos os estados");

        List<String> roles = new ArrayList<>();
        roles.add("Todos os Roles");
        roles.addAll(Arrays.stream(UserRoleType.values())
            .map(EnumDisplayHelper::getRoleDisplayName)
            .toList());
        comboRoleFiltro.setItems(FXCollections.observableArrayList(roles));
        comboRoleFiltro.setValue("Todos os Roles");

        campoPesquisa.textProperty().addListener((obs, old, val) -> filtrarTabela());
        comboEstado.setOnAction(e -> filtrarTabela());
        comboRoleFiltro.setOnAction(e -> filtrarTabela());

        renderizarTabela();
    }

    @FXML
    private void handleNovo() {
        CriarUtilizadorModalController.show(tabelaContainer.getScene().getWindow(), this::renderizarTabela);
    }

    public void renderizarTabela() {
        filtrarTabela();
    }

    private void filtrarTabela() {
        tabelaContainer.getChildren().clear();

        var todos = userService.getAllIncludingInactive();
        String pesquisa = campoPesquisa != null ? campoPesquisa.getText().toLowerCase().trim() : "";
        String estado = comboEstado != null ? comboEstado.getValue() : "Todos os estados";
        String roleFiltro = comboRoleFiltro != null ? comboRoleFiltro.getValue() : "Todos os Roles";

        var filtrados = todos.stream()
                .filter(u -> pesquisa.isEmpty()
                        || u.getNome().toLowerCase().contains(pesquisa)
                        || u.getEmail().toLowerCase().contains(pesquisa))
                .filter(u -> switch (estado) {
                    case "Ativos"   -> u.isActive();
                    case "Inativos" -> !u.isActive();
                    default         -> true;
                })
                .filter(u -> {
                    if (roleFiltro.equals("Todos os Roles")) return true;
                    if (u.getRoles() == null || u.getRoles().isEmpty()) return false;
                    return u.getRoles().stream().anyMatch(r ->
                            EnumDisplayHelper.getRoleDisplayName(r.getRole()).equals(roleFiltro)
                    );
                })
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

        var colAvatar = headerCol("",         48,  false, false);
        var colNome   = headerCol("Nome/Email",200, true, false);
        var colTurno  = headerCol("Turno",    90,  false, false);
        var colAdmissao= headerCol("Admissão",100, false, false);
        var colRoles  = headerCol("Role(s)",  140, false, false);
        var colEstado = headerCol("Estado",   90,  false, false);
        var colAcoes  = headerCol("Ações",    180, false, true);

        row.getChildren().addAll(colAvatar, colNome, colTurno, colAdmissao, colRoles, colEstado, colAcoes);
        return row;
    }

    private Label headerCol(String texto, double largura, boolean grow, boolean right) {
        var lbl = new Label(texto.toUpperCase());
        lbl.getStyleClass().add("tabela-header-label");
        lbl.setMinWidth(largura);
        if (right) {
            lbl.setAlignment(Pos.CENTER_RIGHT);
        }
        if (grow) {
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
        } else {
            lbl.setPrefWidth(largura);
        }
        return lbl;
    }

    private HBox criarLinhaTabela(User user, int index) {
        var row = new HBox();
        row.getStyleClass().add("tabela-linha");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        // Avatar
        var avatar = criarAvatar(user.getNome(), index);

        // Nome / Email
        var nomeLabel = new Label(user.getNome());
        nomeLabel.getStyleClass().add("celula-nome-principal");
        var emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("celula-nome-subtitulo");
        var nomeBox = new VBox(2, nomeLabel, emailLabel);
        nomeBox.setAlignment(Pos.CENTER_LEFT);
        nomeBox.setMaxWidth(Double.MAX_VALUE);
        nomeBox.setMinWidth(200);
        HBox.setHgrow(nomeBox, Priority.ALWAYS);

        // Turno
        var turnoStr = user.getTurno() != null ? EnumDisplayHelper.getTurnoDisplayName(user.getTurno()) : "—";
        var turnoLabel = new Label(turnoStr);
        turnoLabel.getStyleClass().add("celula-dados");
        turnoLabel.setMinWidth(90);

        // Admissão
        var objAdmissao = user.getDataAdmissao();
        var admissaoStr = objAdmissao != null ? objAdmissao.toString() : "—";
        var admissaoLabel = new Label(admissaoStr);
        admissaoLabel.getStyleClass().add("celula-dados");
        admissaoLabel.setMinWidth(100);

        // Roles
        String rolesStr = user.getRoles() != null && !user.getRoles().isEmpty()
                ? user.getRoles().stream().map(r -> EnumDisplayHelper.getRoleDisplayName(r.getRole())).collect(Collectors.joining(", "))
                : "Sem roles";
        var rolesLabel = new Label(rolesStr);
        rolesLabel.getStyleClass().add("celula-dados");
        rolesLabel.setMinWidth(140);
        rolesLabel.setWrapText(true);

        // Estado
        var badgeEstado = new Label(user.isActive() ? "Ativo" : "Inativo");
        badgeEstado.getStyleClass().addAll("badge", user.isActive() ? "badge-ativo" : "badge-inativo");
        badgeEstado.setMinWidth(90);

        // Ações
        var acoesBox = new HBox(6);
        acoesBox.setAlignment(Pos.CENTER_RIGHT);
        acoesBox.setMinWidth(180);

        var btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("btn-linha-acao");
        btnEditar.setOnAction(e -> EditarUtilizadorModalController.show(user, tabelaContainer.getScene().getWindow(), this::renderizarTabela));

        var btnDesativar = new Button(user.isActive() ? "Desativar" : "Removido");
        if (user.isActive()) {
            btnDesativar.getStyleClass().add("btn-linha-danger");
            btnDesativar.setOnAction(e -> DesativarUtilizadorModalController.show(user, tabelaContainer.getScene().getWindow(), this::renderizarTabela));
        } else {
            btnDesativar.getStyleClass().add("btn-linha-acao");
            btnDesativar.setDisable(true); // User is already inactive
        }

        acoesBox.getChildren().addAll(btnEditar, btnDesativar);

        row.getChildren().addAll(avatar, nomeBox, turnoLabel, admissaoLabel, rolesLabel, badgeEstado, acoesBox);
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
        var icone = new FontIcon(MaterialDesignE.EMOTICON_SAD_OUTLINE);
        icone.setIconSize(52);
        icone.getStyleClass().add("estado-vazio-icone");

        var titulo = new Label("Nenhum utilizador encontrado");
        titulo.getStyleClass().add("estado-vazio-titulo");

        var subtitulo = new Label("Altere os filtros ou clique em \"Novo Utilizador\" para adicionar.");
        subtitulo.getStyleClass().add("estado-vazio-subtitulo");
        subtitulo.setTextAlignment(TextAlignment.CENTER);
        subtitulo.setWrapText(true);

        var caixa = new VBox(12, icone, titulo, subtitulo);
        caixa.getStyleClass().add("estado-vazio");
        VBox.setVgrow(caixa, Priority.ALWAYS);
        return caixa;
    }
}
