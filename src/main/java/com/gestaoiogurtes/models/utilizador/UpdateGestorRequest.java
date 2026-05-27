package com.gestaoiogurtes.models.utilizador;

/** Corpo do PUT /users/gestores/{id} */
public class UpdateGestorRequest {
    public String nome;           // required
    public String dataAdmissao;   // optional
    public String turno;          // optional
    public String novaRole;       // optional — enum: ADMIN | FUNCIONARIO_MP | FUNCIONARIO_OP | GESTOR | CLIENTE

    public UpdateGestorRequest(String nome, String dataAdmissao, String turno, String novaRole) {
        this.nome = nome;
        this.dataAdmissao = dataAdmissao;
        this.turno = turno;
        this.novaRole = novaRole;
    }
}
