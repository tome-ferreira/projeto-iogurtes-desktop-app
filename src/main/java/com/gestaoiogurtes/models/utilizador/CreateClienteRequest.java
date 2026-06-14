package com.gestaoiogurtes.models.utilizador;

public class CreateClienteRequest {
    public String nome;
    public String email;
    public String password;
    public String empresaId;

    public CreateClienteRequest(String nome, String email, String password, String empresaId) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.empresaId = empresaId;
    }
}
