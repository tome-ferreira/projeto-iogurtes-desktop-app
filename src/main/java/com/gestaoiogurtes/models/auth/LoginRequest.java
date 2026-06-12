package com.gestaoiogurtes.models.auth;

/**
 * Corpo do pedido POST /auth/login.
 *
 * <p>Os nomes dos campos coincidem exactamente com as chaves JSON
 * esperadas pelo backend (verificado via GET /v3/api-docs).
 */
public class LoginRequest {

    /** Endereço de email do utilizador. */
    public String email;

    /** Palavra-passe em texto simples (enviada sobre HTTPS). */
    public String password;

    /** Construtor necessário para Gson. */
    public LoginRequest() {}

    /**
     * Construtor de conveniência.
     *
     * @param email    endereço de email
     * @param password palavra-passe
     */
    public LoginRequest(String email, String password) {
        this.email    = email;
        this.password = password;
    }
}
