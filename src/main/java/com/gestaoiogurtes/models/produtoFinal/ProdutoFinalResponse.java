package com.gestaoiogurtes.models.produtoFinal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para Produto Final.
 * Campos coincidem EXACTAMENTE com as chaves JSON da API.
 */
public class ProdutoFinalResponse {
    public UUID          id;
    public String        codigoSku;
    public String        nome;
    public String        descricao;
    public String        abreviacaoSabor;
    /** Enum: LIQUIDO | SOLIDO */
    public String        estadoFisico;
    public Integer       validadeDias;
    public Double        precoVenda;
    public Double        precoPorKg;
    public Double        taxaIva;
    public Boolean       visivelCliente;
    public Integer       quantidadeLote;
    public List<ProdutoMateriaResponse> composicao;
    public Boolean       isActive;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public ProdutoFinalResponse() {}
}
