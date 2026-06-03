package com.gestaoiogurtes.models.produtoFinal;

import java.util.List;

/**
 * Corpo do POST /produtos-finais.
 * Campos coincidem EXACTAMENTE com as chaves JSON da API (CreateProdutoFinalRequest).
 * Campo obrigatórios: nome, abreviacaoSabor, estadoFisico, taxaIva, visivelCliente,
 *                      quantidadeLote, composicao.
 */
public class CreateProdutoFinalRequest {
    public String  nome;
    public String  descricao;
    /** Exactamente 3 caracteres A-Za-z. Ex: "MOR" */
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
    /** Obrigatório (pode ser vazia, mas a API exige o campo) */
    public List<ComposicaoItem> composicao;

    public CreateProdutoFinalRequest() {}
}
