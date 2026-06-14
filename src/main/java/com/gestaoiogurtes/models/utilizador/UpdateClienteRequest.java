package com.gestaoiogurtes.models.utilizador;

public class UpdateClienteRequest {
    public String nome;
    public String empresaId;

    public UpdateClienteRequest(String nome, String empresaId) {
        this.nome = nome;
        this.empresaId = empresaId;
    }
}
