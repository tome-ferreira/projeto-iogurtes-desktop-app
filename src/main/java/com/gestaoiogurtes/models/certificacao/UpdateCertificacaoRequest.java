package com.gestaoiogurtes.models.certificacao;

public class UpdateCertificacaoRequest {
    public String nome;
    public String descricao;

    public UpdateCertificacaoRequest(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
}
