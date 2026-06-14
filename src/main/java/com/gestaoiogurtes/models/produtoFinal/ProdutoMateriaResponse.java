package com.gestaoiogurtes.models.produtoFinal;

import java.util.UUID;

public class ProdutoMateriaResponse {
    public UUID id;
    public UUID materiaId;
    public String materiaNome;
    public String materiaUnidade;
    public Double quantidadePorUnidadeProduto;

    public ProdutoMateriaResponse() {
    }
}
