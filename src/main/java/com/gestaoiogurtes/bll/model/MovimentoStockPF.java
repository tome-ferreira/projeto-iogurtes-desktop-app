//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.TipoMovimentoPF;

/**
 * Movimento de stock de um produto final (produção, expedição, ajuste ou devolução).
 */
public class MovimentoStockPF extends BaseEntity {

    private ProdutoFinal produto;
    private OrdemProducao ordem;
    private TipoMovimentoPF tipo;
    private Integer quantidadeKg;
    private String observacao;

    public MovimentoStockPF() {}

    public MovimentoStockPF(ProdutoFinal produto, OrdemProducao ordem,
                            TipoMovimentoPF tipo, Integer quantidadeKg,
                            String observacao) {
        this.produto     = produto;
        this.ordem       = ordem;
        this.tipo        = tipo;
        this.quantidadeKg = quantidadeKg;
        this.observacao  = observacao;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public ProdutoFinal    getProduto()                       { return produto; }
    public void            setProduto(ProdutoFinal p)         { this.produto = p; }

    public OrdemProducao   getOrdem()                         { return ordem; }
    public void            setOrdem(OrdemProducao o)          { this.ordem = o; }

    public TipoMovimentoPF getTipo()                          { return tipo; }
    public void            setTipo(TipoMovimentoPF tipo)      { this.tipo = tipo; }

    public Integer         getQuantidadeKg()                  { return quantidadeKg; }
    public void            setQuantidadeKg(Integer q)         { this.quantidadeKg = q; }

    public String          getObservacao()                    { return observacao; }
    public void            setObservacao(String obs)          { this.observacao = obs; }
}
