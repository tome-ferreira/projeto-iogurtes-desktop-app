package com.gestaoiogurtes.models.utilizador;

/**
 * Corpo do POST /users/funcionarios/op e POST /users/funcionarios/mp.
 * O mesmo schema é partilhado pelos dois tipos.
 */
public class CreateFuncionarioRequest {
    public String nome;
    public String email;
    public String password;
    public String turno;          // required — "MANHA" | "TARDE" | "NOITE"
    public String dataAdmissao;   // optional — format: date (yyyy-MM-dd)

    public CreateFuncionarioRequest(String nome, String email, String password, String turno, String dataAdmissao) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.turno = turno;
        this.dataAdmissao = dataAdmissao;
    }
}
