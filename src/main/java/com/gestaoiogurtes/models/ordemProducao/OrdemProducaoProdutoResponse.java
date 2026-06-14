package com.gestaoiogurtes.models.ordemProducao;

import java.util.UUID;

public class OrdemProducaoProdutoResponse {
    public UUID id;
    public UUID produtoId;
    public String produtoNome;
    public String produtoSku;
    public Double quantidadeKg;

    public OrdemProducaoProdutoResponse() {}
}
