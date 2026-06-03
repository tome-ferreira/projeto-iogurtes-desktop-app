package com.gestaoiogurtes.models.encomenda;

import java.util.UUID;

public class EncomendaPalletResponse {
    public UUID id;
    public UUID produtoId;
    public String produtoNome;
    public String produtoSku;
    public UUID palletTipoId;
    public String palletTipoNome;
    public Double palletCapacidadeKg;
    public Integer quantidadePallets;
    public Double precoPorPalletEur;
    public Double taxaIva;
    public Double subtotalEur;
    public Double subtotalComIvaEur;

    public EncomendaPalletResponse() {}
}
