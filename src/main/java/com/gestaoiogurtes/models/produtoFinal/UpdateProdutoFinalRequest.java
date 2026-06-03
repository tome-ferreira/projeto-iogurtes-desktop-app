package com.gestaoiogurtes.models.produtoFinal;

/**
 * Corpo do PUT /produtos-finais/{id}.
 * Campos coincidem EXACTAMENTE com as chaves JSON da API (UpdateProdutoFinalRequest).
 * NÃO inclui composicao — a API não aceita composicao no PUT.
 * Campos obrigatórios: nome, abreviacaoSabor, estadoFisico, taxaIva,
 *                       visivelCliente, quantidadeLote.
 */
public class UpdateProdutoFinalRequest {
    public String  nome;
    public String  descricao;
    /** Exactamente 3 caracteres A-Za-z */
    public String  abreviacaoSabor;
    /** Enum: "LIQUIDO" | "SOLIDO" */
    public String  estadoFisico;
    public Integer validadeDias;
    public Double  precoVenda;
    public Double  precoPorKg;
    /** Obrigatório, mínimo 0.0 */
    public Double  taxaIva;
    /** Obrigatório */
    public Boolean visivelCliente;
    /** Obrigatório, mínimo 1 */
    public Integer quantidadeLote;

    public UpdateProdutoFinalRequest() {}
}
