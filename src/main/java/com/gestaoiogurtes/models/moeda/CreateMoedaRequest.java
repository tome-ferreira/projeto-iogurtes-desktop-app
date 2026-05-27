package com.gestaoiogurtes.models.moeda;

public class CreateMoedaRequest {
    public String codigo;
    public String nome;
    public String simbolo;
    public Double taxaConversaoEur;

    public CreateMoedaRequest(String codigo, String nome, String simbolo, Double taxaConversaoEur) {
        this.codigo = codigo;
        this.nome = nome;
        this.simbolo = simbolo;
        this.taxaConversaoEur = taxaConversaoEur;
    }
}
