package com.gestaoiogurtes.models.utilizador;

public class CreateAdminRequest {
    public String nome;
    public String email;
    public String password;

    public CreateAdminRequest(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }
}
