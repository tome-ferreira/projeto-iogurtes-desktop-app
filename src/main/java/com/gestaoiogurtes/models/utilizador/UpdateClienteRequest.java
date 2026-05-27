package com.gestaoiogurtes.models.utilizador;

/** Corpo do PUT /users/clientes/{id} */
public class UpdateClienteRequest {
    public String nome;      // required
    public String empresaId; // optional — UUID as String

    public UpdateClienteRequest(String nome, String empresaId) {
        this.nome = nome;
        this.empresaId = empresaId;
    }
}
