package com.gestaoiogurtes.models.fornecedortipo;

public class CreateFornecedorTipoRequest {
    public String nome;
    public String descricao;

    public CreateFornecedorTipoRequest(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
}
