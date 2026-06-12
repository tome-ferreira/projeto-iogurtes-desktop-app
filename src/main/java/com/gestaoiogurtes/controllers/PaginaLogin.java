package com.gestaoiogurtes.controllers;

import com.gestaoiogurtes.GestaoIogurtes;
import com.gestaoiogurtes.models.auth.LoginResponse;
import com.gestaoiogurtes.services.AuthService;
import com.gestaoiogurtes.utils.AppAware;
import com.gestaoiogurtes.utils.NavigationHelper;
import com.gestaoiogurtes.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for PaginaLogin.fxml.
 *
 * <p>Autentica o utilizador contra o endpoint POST /auth/login do backend.
 * Em caso de sucesso, guarda todos os dados de sessão no {@link SessionManager}
 * e navega para o dashboard correspondente ao role recebido na resposta.
 *
 * <p>Em caso de erro (credenciais inválidas, falha de rede), apresenta
 * a mensagem de erro no {@code lblErro} e reactiva o botão de entrada.
 */
public class PaginaLogin implements AppAware {

    @FXML private TextField     campoEmail;
    @FXML private PasswordField campoPassword;
    @FXML private Button        btnEntrar;
    @FXML private Label         lblErro;

    private GestaoIogurtes app;
    private final AuthService authService = new AuthService();

    // ── AppAware ──────────────────────────────────────────────────────────────

    @Override
    public void setApp(GestaoIogurtes app) {
        this.app = app;
    }

    // ── FXML handler ──────────────────────────────────────────────────────────

    @FXML
    private void handleLogin() {
        String email    = campoEmail.getText();
        String password = campoPassword.getText();

        // Ocultar erro anterior, se existir
        lblErro.setVisible(false);
        lblErro.setManaged(false);

        authService.login(email, password, state -> {
            if (state.isLoading()) {
                btnEntrar.setDisable(true);
                btnEntrar.setText("A entrar...");

            } else if (state.isSuccess()) {
                LoginResponse resposta = state.getData();

                // Utilizadores CLIENTE não têm acesso à aplicação desktop
                if ("CLIENTE".equals(resposta.role)) {
                    mostrarErroLogin();
                    return;
                }

                // Guardar todos os dados de sessão
                SessionManager session = SessionManager.getInstance();
                session.setUserId(resposta.id);
                session.setUserName(resposta.nome);
                session.setUserEmail(resposta.email);
                session.setUserRole(resposta.role);
                session.setAuthToken(resposta.token);

                // Navegar para o dashboard correcto conforme o role
                navegarParaDashboard(resposta.role);

            } else if (state.isError()) {
                mostrarErroLogin();
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Apresenta a mensagem de erro genérica, limpa o formulário e
     * reactiva o botão de entrada.
     *
     * <p>Chamado tanto em caso de erro HTTP/rede como quando o role
     * devolvido pelo backend não tem acesso à aplicação desktop (CLIENTE).
     */
    private void mostrarErroLogin() {
        campoEmail.clear();
        campoPassword.clear();
        btnEntrar.setDisable(false);
        btnEntrar.setText("Entrar");
        lblErro.setText("Login inválido.");
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    // ── Navegação por role ────────────────────────────────────────────────────

    private void navegarParaDashboard(String role) {
        if (role == null) {
            NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
            return;
        }
        switch (role) {
            case "ADMIN"         -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardAdmin.fxml");
            case "GESTOR"        -> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardGestor.fxml");
            case "FUNCIONARIO_MP"-> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioMp.fxml");
            case "FUNCIONARIO_OP"-> NavigationHelper.navigateTo(app, "/fxml/paginas/DashboardFuncionarioOp.fxml");
            default              -> NavigationHelper.navigateTo(app, "/fxml/paginas/Dashboard.fxml");
        }
    }
}
