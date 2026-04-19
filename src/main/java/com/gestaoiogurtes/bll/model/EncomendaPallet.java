//v1.0
package com.gestaoiogurtes.bll.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Linha de pallet dentro de uma encomenda.
 */
public class EncomendaPallet extends BaseEntity {

    private Encomenda encomenda;
    private ProdutoFinal produto;
    private PalletTipo palletTipo;
    private Integer quantidadePallets;
    private BigDecimal precoPorPallet;
    private List<EncomendaOrdem> ordens = new ArrayList<>();

    public EncomendaPallet() {}

    // ── Getters / setters ──────────────────────────────────────────

    public Encomenda     getEncomenda()                          { return encomenda; }
    public void          setEncomenda(Encomenda e)               { this.encomenda = e; }

    public ProdutoFinal  getProduto()                            { return produto; }
    public void          setProduto(ProdutoFinal p)              { this.produto = p; }

    public PalletTipo    getPalletTipo()                         { return palletTipo; }
    public void          setPalletTipo(PalletTipo pt)            { this.palletTipo = pt; }

    public Integer       getQuantidadePallets()                  { return quantidadePallets; }
    public void          setQuantidadePallets(Integer q)         { this.quantidadePallets = q; }

    public BigDecimal    getPrecoPorPallet()                     { return precoPorPallet; }
    public void          setPrecoPorPallet(BigDecimal preco)     { this.precoPorPallet = preco; }

    public List<EncomendaOrdem> getOrdens()                      { return ordens; }
    public void          setOrdens(List<EncomendaOrdem> ordens)  { this.ordens = ordens; }
}
