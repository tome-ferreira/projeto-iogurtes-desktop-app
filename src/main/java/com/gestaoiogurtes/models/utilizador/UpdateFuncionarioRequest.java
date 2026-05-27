package com.gestaoiogurtes.models.utilizador;

/** Corpo do PUT /users/funcionarios/{id} */
public class UpdateFuncionarioRequest {
    public String nome;          // required
    public String turno;         // optional — "MANHA" | "TARDE" | "NOITE"
    public String dataAdmissao;  // optional — format: date (yyyy-MM-dd)
    public String novaRole;      // optional — enum: ADMIN | FUNCIONARIO_MP | FUNCIONARIO_OP | GESTOR | CLIENTE

    public UpdateFuncionarioRequest(String nome, String turno, String dataAdmissao, String novaRole) {
        this.nome = nome;
        this.turno = turno;
        this.dataAdmissao = dataAdmissao;
        this.novaRole = novaRole;
    }
}
