//v1.0
package com.gestaoiogurtes.bll.model;

import java.math.BigDecimal;

/**
 * Linha de produto de uma ordem de produção.
 */
public class OrdemProducaoProduto extends BaseEntity {

    private OrdemProducao ordem;
    private ProdutoFinal produto;
    private BigDecimal quantidadeKg;

    public OrdemProducaoProduto() {}

    public OrdemProducaoProduto(OrdemProducao ordem, ProdutoFinal produto, BigDecimal quantidadeKg) {
        this.ordem       = ordem;
        this.produto     = produto;
        this.quantidadeKg = quantidadeKg;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public OrdemProducao  getOrdem()                      { return ordem; }
    public void           setOrdem(OrdemProducao o)       { this.ordem = o; }

    public ProdutoFinal   getProduto()                    { return produto; }
    public void           setProduto(ProdutoFinal p)      { this.produto = p; }

    public BigDecimal     getQuantidadeKg()               { return quantidadeKg; }
    public void           setQuantidadeKg(BigDecimal q)   { this.quantidadeKg = q; }
}
