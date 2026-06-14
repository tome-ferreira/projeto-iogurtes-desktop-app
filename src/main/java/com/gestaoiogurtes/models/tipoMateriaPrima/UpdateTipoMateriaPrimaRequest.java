package com.gestaoiogurtes.models.tipoMateriaPrima;

public class UpdateTipoMateriaPrimaRequest {
    public String nome;
    public String descricao;
    public Double taxaIva;

    public UpdateTipoMateriaPrimaRequest() {}

    public UpdateTipoMateriaPrimaRequest(String nome, String descricao, Double taxaIva) {
        this.nome = nome;
        this.descricao = descricao;
        this.taxaIva = taxaIva;
    }
}
