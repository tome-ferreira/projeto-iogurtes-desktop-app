package com.gestaoiogurtes.models.ordemProducao;

import java.util.UUID;

public class OrdemProducaoLinhaItem {
    public UUID produtoId;
    public Double quantidadeKg;

    public OrdemProducaoLinhaItem() {}

    public OrdemProducaoLinhaItem(UUID produtoId, Double quantidadeKg) {
        this.produtoId = produtoId;
        this.quantidadeKg = quantidadeKg;
    }
}
