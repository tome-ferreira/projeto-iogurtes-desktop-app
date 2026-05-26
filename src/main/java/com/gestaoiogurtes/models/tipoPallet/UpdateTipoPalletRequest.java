package com.gestaoiogurtes.models.tipoPallet;

public class UpdateTipoPalletRequest {
    public String nome;
    public Double capacidadeKg;

    public UpdateTipoPalletRequest(String nome, Double capacidadeKg) {
        this.nome = nome;
        this.capacidadeKg = capacidadeKg;
    }
}
