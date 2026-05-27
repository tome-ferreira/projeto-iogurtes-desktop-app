package com.gestaoiogurtes.models.utilizador;

/** Corpo do PUT /users/admins/{id} */
public class UpdateAdminRequest {
    public String nome; // required

    public UpdateAdminRequest(String nome) {
        this.nome = nome;
    }
}
