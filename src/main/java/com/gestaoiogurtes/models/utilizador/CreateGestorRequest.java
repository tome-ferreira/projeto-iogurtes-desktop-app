package com.gestaoiogurtes.models.utilizador;

public class CreateGestorRequest {
    public String nome;
    public String email;
    public String password;
    public String dataAdmissao;

    public CreateGestorRequest(String nome, String email, String password, String dataAdmissao) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.dataAdmissao = dataAdmissao;
    }
}
