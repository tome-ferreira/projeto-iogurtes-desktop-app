package com.gestaoiogurtes.models.certificacao;

public class CreateCertificacaoRequest {
    public String nome;
    public String descricao;

    public CreateCertificacaoRequest(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
}
