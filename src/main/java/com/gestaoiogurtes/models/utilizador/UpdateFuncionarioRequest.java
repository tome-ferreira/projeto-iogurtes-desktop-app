package com.gestaoiogurtes.models.utilizador;

public class UpdateFuncionarioRequest {
    public String nome;
    public String turno;
    public String dataAdmissao;
    public String novaRole;

    public UpdateFuncionarioRequest(String nome, String turno, String dataAdmissao, String novaRole) {
        this.nome = nome;
        this.turno = turno;
        this.dataAdmissao = dataAdmissao;
        this.novaRole = novaRole;
    }
}
