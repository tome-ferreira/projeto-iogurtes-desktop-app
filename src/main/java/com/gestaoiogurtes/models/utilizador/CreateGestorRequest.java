package com.gestaoiogurtes.models.utilizador;

/** Corpo do POST /users/gestores */
public class CreateGestorRequest {
    public String nome;
    public String email;
    public String password;
    public String dataAdmissao; // optional, format: date (yyyy-MM-dd)

    public CreateGestorRequest(String nome, String email, String password, String dataAdmissao) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.dataAdmissao = dataAdmissao;
    }
}
