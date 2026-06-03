package com.gestaoiogurtes.models.produtoFinal;

import java.util.UUID;

public class CreateProdutoMateriaRequest {
    public UUID materiaId;
    public Double quantidadePorUnidadeProduto;

    public CreateProdutoMateriaRequest() {}

    public CreateProdutoMateriaRequest(UUID materiaId, Double quantidade) {
        this.materiaId = materiaId;
        this.quantidadePorUnidadeProduto = quantidade;
    }
}
