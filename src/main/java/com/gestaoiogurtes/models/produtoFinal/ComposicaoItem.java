package com.gestaoiogurtes.models.produtoFinal;

import java.util.UUID;

public class ComposicaoItem {
    public UUID materiaId;
    public Double quantidadePorUnidadeProduto;

    public ComposicaoItem() {
    }

    public ComposicaoItem(UUID materiaId, Double quantidadePorUnidadeProduto) {
        this.materiaId = materiaId;
        this.quantidadePorUnidadeProduto = quantidadePorUnidadeProduto;
    }
}
