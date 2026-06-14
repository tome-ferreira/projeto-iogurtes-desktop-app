package com.gestaoiogurtes.models.moeda;

public class UpdateMoedaRequest {
    public String nome;
    public String simbolo;
    public Double taxaConversaoEur;

    public UpdateMoedaRequest(String nome, String simbolo, Double taxaConversaoEur) {
        this.nome = nome;
        this.simbolo = simbolo;
        this.taxaConversaoEur = taxaConversaoEur;
    }
}
