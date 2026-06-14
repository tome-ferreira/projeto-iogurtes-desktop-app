package com.gestaoiogurtes.models.utilizador;

public class UpdateAdminRequest {
    public String nome; // required

    public UpdateAdminRequest(String nome) {
        this.nome = nome;
    }
}
