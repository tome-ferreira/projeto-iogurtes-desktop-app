package com.gestaoiogurtes.models.tipoPallet;

public class CreateTipoPalletRequest {
    public String nome;
    public Double capacidadeKg;

    public CreateTipoPalletRequest(String nome, Double capacidadeKg) {
        this.nome = nome;
        this.capacidadeKg = capacidadeKg;
    }
}
