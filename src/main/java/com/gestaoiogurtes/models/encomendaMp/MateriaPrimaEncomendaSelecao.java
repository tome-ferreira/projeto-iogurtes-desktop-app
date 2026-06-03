package com.gestaoiogurtes.models.encomendaMp;

/**
 * Resultado da selecção de matéria prima no modal de criação de encomenda MP.
 * Contém os dados necessários para apresentar a linha e construir o pedido.
 */
public class MateriaPrimaEncomendaSelecao {
    /** UUID da relação MateriaPrimaFornecedor (usado para exibição). */
    public final String id;
    /** Nome da matéria prima. */
    public final String nome;
    /** Preço unitário na moeda do fornecedor. */
    public final Double precoUnitario;
    /** Símbolo da moeda (ex: "€", "$"). */
    public final String moedaSimbolo;
    /** Quantidade introduzida pelo utilizador. */
    public final Double quantidade;

    public MateriaPrimaEncomendaSelecao(
            String id,
            String nome,
            Double precoUnitario,
            String moedaSimbolo,
            Double quantidade) {
        this.id           = id;
        this.nome         = nome;
        this.precoUnitario = precoUnitario;
        this.moedaSimbolo  = moedaSimbolo;
        this.quantidade    = quantidade;
    }
}
