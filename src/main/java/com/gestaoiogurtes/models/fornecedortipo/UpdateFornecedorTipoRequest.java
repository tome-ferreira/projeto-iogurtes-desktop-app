package com.gestaoiogurtes.models.fornecedortipo;

public class UpdateFornecedorTipoRequest {
    public String nome;
    public String descricao;

    public UpdateFornecedorTipoRequest(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
}
