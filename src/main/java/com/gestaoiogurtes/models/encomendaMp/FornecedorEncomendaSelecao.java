package com.gestaoiogurtes.models.encomendaMp;

/**
 * Resultado da selecção de fornecedor no modal de criação de encomenda MP.
 */
public class FornecedorEncomendaSelecao {
    public final String id;
    public final String nome;

    public FornecedorEncomendaSelecao(String id, String nome) {
        this.id   = id;
        this.nome = nome;
    }
}
