package com.gestaoiogurtes.models.auth;

import java.util.UUID;

/**
 * Corpo da resposta de POST /auth/login.
 *
 * <p>Os nomes dos campos coincidem exactamente com as chaves JSON
 * devolvidas pelo backend (verificado via GET /v3/api-docs):
 * <ul>
 *   <li>{@code id}    — UUID do utilizador autenticado</li>
 *   <li>{@code nome}  — nome completo do utilizador</li>
 *   <li>{@code email} — endereço de email</li>
 *   <li>{@code role}  — papel do utilizador (ADMIN, GESTOR, etc.)</li>
 *   <li>{@code token} — JWT assinado com HS256 para usar nas chamadas seguintes</li>
 * </ul>
 */
public class LoginResponse {

    /** UUID único do utilizador. */
    public UUID id;

    /** Nome completo do utilizador. */
    public String nome;

    /** Endereço de email do utilizador. */
    public String email;

    /**
     * Papel do utilizador no sistema.
     * Valores esperados: {@code ADMIN}, {@code GESTOR},
     * {@code FUNCIONARIO_MP}, {@code FUNCIONARIO_OP}, {@code CLIENTE}.
     */
    public String role;

    /**
     * Token JWT (HS256) a incluir em todos os pedidos autenticados
     * como {@code Authorization: Bearer <token>}.
     */
    public String token;

    /** Construtor necessário para Gson. */
    public LoginResponse() {}
}
