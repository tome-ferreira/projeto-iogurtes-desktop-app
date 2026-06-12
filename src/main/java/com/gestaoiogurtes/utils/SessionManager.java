package com.gestaoiogurtes.utils;

import java.util.UUID;

/**
 * Serviço singleton para acesso global a dados da sessão do utilizador.
 *
 * <p>Populado no login ({@link com.gestaoiogurtes.controllers.PaginaLogin})
 * e limpo no logout ({@link com.gestaoiogurtes.layout.Sidebar#handleSair()})
 * através de {@link #clearSession()}.
 *
 * <h3>Utilização</h3>
 * <pre>{@code
 * // Ler
 * UUID   id    = SessionManager.getInstance().getUserId();
 * String role  = SessionManager.getInstance().getUserRole();
 * String name  = SessionManager.getInstance().getUserName();
 * String email = SessionManager.getInstance().getUserEmail();
 * String token = SessionManager.getInstance().getAuthToken();
 *
 * // Escrever
 * SessionManager.getInstance().setUserRole("GESTOR");
 * }</pre>
 */
public class SessionManager {

    private static SessionManager instance;

    private UUID   userId;
    private String userRole;
    private String userName;
    private String userEmail;
    private String authToken;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public UUID   getUserId()    { return userId; }
    public String getUserRole()  { return userRole; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }

    /**
     * Devolve o token JWT activo, ou {@code null} se não existir sessão.
     *
     * <p>O {@link com.gestaoiogurtes.api.RetrofitClient} lê este valor
     * automaticamente em cada pedido e adiciona o header
     * {@code Authorization: Bearer <token>}.
     *
     * @return token JWT, ou {@code null} antes do login ou após logout
     */
    public String getAuthToken() { return authToken; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setUserId(UUID userId)       { this.userId    = userId; }
    public void setUserRole(String userRole) { this.userRole  = userRole; }
    public void setUserName(String userName) { this.userName  = userName; }
    public void setUserEmail(String email)   { this.userEmail = email; }

    /**
     * Guarda o token JWT recebido no login.
     * Após este set, o {@link com.gestaoiogurtes.api.RetrofitClient}
     * incluirá automaticamente o header de autorização em todos os pedidos.
     *
     * @param token token JWT devolvido pelo backend em POST /auth/login
     */
    public void setAuthToken(String token)   { this.authToken = token; }

    // ── Sessão ───────────────────────────────────────────────────────────────

    /**
     * Limpa todos os dados da sessão activa.
     *
     * <p>Deve ser chamado ao fazer logout (antes de navegar para PaginaLogin).
     * Após este método:
     * <ul>
     *   <li>O interceptor do RetrofitClient deixará de enviar o header Authorization.</li>
     *   <li>Todos os campos da sessão ficam {@code null}.</li>
     * </ul>
     */
    public void clearSession() {
        this.userId    = null;
        this.userName  = null;
        this.userRole  = null;
        this.userEmail = null;
        this.authToken = null;
    }
}