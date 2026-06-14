package com.gestaoiogurtes.models.ordemProducao;

public class ProdutoOrdemSelecao {
    public final String id;
    public final String nome;
    public final Double quantidade;

    public ProdutoOrdemSelecao(String id, String nome, Double quantidade) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
    }
}
