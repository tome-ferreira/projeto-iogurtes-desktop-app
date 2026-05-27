package com.gestaoiogurtes.models.utilizador;

/** Corpo do POST /users/clientes */
public class CreateClienteRequest {
    public String nome;
    public String email;
    public String password;
    public String empresaId; // UUID as String — required

    public CreateClienteRequest(String nome, String email, String password, String empresaId) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.empresaId = empresaId;
    }
}
