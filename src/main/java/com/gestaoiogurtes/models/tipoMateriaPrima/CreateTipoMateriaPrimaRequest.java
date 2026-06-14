package com.gestaoiogurtes.models.tipoMateriaPrima;

public class CreateTipoMateriaPrimaRequest {
    public String nome;
    public String descricao;
    public Double taxaIva;

    public CreateTipoMateriaPrimaRequest() {}

    public CreateTipoMateriaPrimaRequest(String nome, String descricao, Double taxaIva) {
        this.nome = nome;
        this.descricao = descricao;
        this.taxaIva = taxaIva;
    }
}
