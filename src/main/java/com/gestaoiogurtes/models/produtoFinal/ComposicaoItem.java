package com.gestaoiogurtes.models.produtoFinal;

import java.util.UUID;

/**
 * Item da composição enviado no POST /produtos-finais.
 * Campos coincidem EXACTAMENTE com as chaves JSON da API (CreateProdutoMateriaRequest).
 */
public class ComposicaoItem {
    /** UUID da matéria prima — campo JSON: "materiaId" */
    public UUID   materiaId;
    /** Quantidade por unidade de produto — campo JSON: "quantidadePorUnidadeProduto" */
    public Double quantidadePorUnidadeProduto;

    public ComposicaoItem() {}

    public ComposicaoItem(UUID materiaId, Double quantidadePorUnidadeProduto) {
        this.materiaId = materiaId;
        this.quantidadePorUnidadeProduto = quantidadePorUnidadeProduto;
    }
}
