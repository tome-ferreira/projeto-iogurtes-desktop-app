package com.gestaoiogurtes.models.utilizador;

import java.util.UUID;

/**
 * Resposta unificada para qualquer tipo de utilizador.
 * Campos opcionais (turno, empresaId, dataAdmissao) podem ser null
 * consoante o tipo de utilizador (role).
 */
public class UserResponse {
    public UUID   id;
    public String nome;
    public String email;
    public String role;         // "ADMIN" | "GESTOR" | "CLIENTE" | "FUNCIONARIO_OP" | "FUNCIONARIO_MP"
    public String turno;        // nullable — apenas Funcionários e Gestores
    public String empresaId;    // nullable — apenas Clientes
    public String dataAdmissao; // nullable — Gestores e Funcionários
    public String createdAt;

    public UserResponse() {}
}
