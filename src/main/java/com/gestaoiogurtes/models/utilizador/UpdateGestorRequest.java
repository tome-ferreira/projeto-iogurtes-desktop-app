package com.gestaoiogurtes.models.utilizador;

public class UpdateGestorRequest {
    public String nome;
    public String dataAdmissao;
    public String turno;
    public String novaRole;

    public UpdateGestorRequest(String nome, String dataAdmissao, String turno, String novaRole) {
        this.nome = nome;
        this.dataAdmissao = dataAdmissao;
        this.turno = turno;
        this.novaRole = novaRole;
    }
}
