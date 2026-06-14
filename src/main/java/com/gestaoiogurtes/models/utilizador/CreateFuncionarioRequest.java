package com.gestaoiogurtes.models.utilizador;

public class CreateFuncionarioRequest {
    public String nome;
    public String email;
    public String password;
    public String turno;
    public String dataAdmissao;

    public CreateFuncionarioRequest(String nome, String email, String password, String turno, String dataAdmissao) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.turno = turno;
        this.dataAdmissao = dataAdmissao;
    }
}
