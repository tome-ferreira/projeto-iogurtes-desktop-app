package com.gestaoiogurtes.models.produtoFinal;

import java.util.UUID;

/**
 * DTO de resposta para um item da composição de produto final.
 * Campos coincidem EXACTAMENTE com as chaves JSON da API (ProdutoMateriaResponse).
 */
public class ProdutoMateriaResponse {
    public UUID   id;
    public UUID   materiaId;
    public String materiaNome;
    public String materiaUnidade;
    public Double quantidadePorUnidadeProduto;

    public ProdutoMateriaResponse() {}
}
