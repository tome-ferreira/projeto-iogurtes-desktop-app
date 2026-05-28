package com.gestaoiogurtes.models.fornecedor;

public class UpdateFornecedorRequest {
    public String nome;
    public String nif;
    public String email;
    public String telefone;
    public String morada;
    public String cidade;
    public String tipoId;

    public UpdateFornecedorRequest() {}

    public UpdateFornecedorRequest(String nome, String nif, String email, String telefone, String morada, String cidade, String tipoId) {
        this.nome = nome;
        this.nif = nif;
        this.email = email;
        this.telefone = telefone;
        this.morada = morada;
        this.cidade = cidade;
        this.tipoId = tipoId;
    }
}
