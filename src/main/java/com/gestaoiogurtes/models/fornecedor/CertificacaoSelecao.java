package com.gestaoiogurtes.models.fornecedor;

/**
 * POJO de selecção devolvido pelo SelecionarCertificacaoModalController.
 */
public class CertificacaoSelecao {
    public final String id;
    public final String nome;

    public CertificacaoSelecao(String id, String nome) {
        this.id   = id;
        this.nome = nome;
    }
}
