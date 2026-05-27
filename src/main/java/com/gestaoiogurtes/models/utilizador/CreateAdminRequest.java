package com.gestaoiogurtes.models.utilizador;

/** Corpo do POST /users/admins */
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
