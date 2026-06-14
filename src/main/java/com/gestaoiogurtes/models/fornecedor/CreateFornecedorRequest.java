package com.gestaoiogurtes.models.fornecedor;

import java.util.List;

public class CreateFornecedorRequest {
    public String nome;
    public String nif;
    public String email;
    public String telefone;
    public String morada;
    public String cidade;
    public String tipoId;
    public List<Object> certificacoes;

    public CreateFornecedorRequest() {}

    public CreateFornecedorRequest(String nome, String nif, String email, String telefone, String morada, String cidade, String tipoId, List<Object> certificacoes) {
        this.nome = nome;
        this.nif = nif;
        this.email = email;
        this.telefone = telefone;
        this.morada = morada;
        this.cidade = cidade;
        this.tipoId = tipoId;
        this.certificacoes = certificacoes;
    }
}
