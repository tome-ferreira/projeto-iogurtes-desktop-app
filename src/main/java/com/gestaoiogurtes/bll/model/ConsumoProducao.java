//v1.0
package com.gestaoiogurtes.bll.model;

import java.math.BigDecimal;

/**
 * Registo do consumo de uma matéria-prima por uma ordem de produção.
 */
public class ConsumoProducao extends BaseEntity {

    private OrdemProducao ordem;
    private MateriaPrima materia;
    private BigDecimal quantidadeKg;

    public ConsumoProducao() {}

    public ConsumoProducao(OrdemProducao ordem, MateriaPrima materia, BigDecimal quantidadeKg) {
        this.ordem       = ordem;
        this.materia     = materia;
        this.quantidadeKg = quantidadeKg;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public OrdemProducao  getOrdem()                         { return ordem; }
    public void           setOrdem(OrdemProducao o)          { this.ordem = o; }

    public MateriaPrima   getMateria()                       { return materia; }
    public void           setMateria(MateriaPrima m)         { this.materia = m; }

    public BigDecimal     getQuantidadeKg()                  { return quantidadeKg; }
    public void           setQuantidadeKg(BigDecimal q)      { this.quantidadeKg = q; }
}
