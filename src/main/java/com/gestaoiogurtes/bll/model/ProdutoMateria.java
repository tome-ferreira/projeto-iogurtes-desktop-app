//v1.0
package com.gestaoiogurtes.bll.model;

import java.math.BigDecimal;

/**
 * Ingrediente (matéria-prima) associado a um produto final.
 */
public class ProdutoMateria extends BaseEntity {

    private ProdutoFinal produto;
    private MateriaPrima materia;
    private BigDecimal quantidadePorUnidadeProduto;

    public ProdutoMateria() {}

    // ── Getters / setters ──────────────────────────────────────────

    public ProdutoFinal  getProduto()                          { return produto; }
    public void          setProduto(ProdutoFinal p)            { this.produto = p; }

    public MateriaPrima  getMateria()                          { return materia; }
    public void          setMateria(MateriaPrima m)            { this.materia = m; }

    public BigDecimal    getQuantidadePorUnidadeProduto()      { return quantidadePorUnidadeProduto; }
    public void          setQuantidadePorUnidadeProduto(BigDecimal q) { this.quantidadePorUnidadeProduto = q; }
}
