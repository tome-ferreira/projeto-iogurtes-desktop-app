package com.gestaoiogurtes.models.encomendaMp;

public class MateriaPrimaEncomendaSelecao {

    public final String id;

    public final String nome;
    public final Double precoUnitario;
    public final String moedaSimbolo;
    public final Double quantidade;

    public MateriaPrimaEncomendaSelecao(
            String id,
            String nome,
            Double precoUnitario,
            String moedaSimbolo,
            Double quantidade) {
        this.id = id;
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.moedaSimbolo = moedaSimbolo;
        this.quantidade = quantidade;
    }
}
