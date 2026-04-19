//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.TipoMovimentoMP;

import java.math.BigDecimal;

/**
 * Movimento de stock de uma matéria-prima (entrada, saída ou ajuste).
 */
public class MovimentoStockMP extends BaseEntity {

    private User user;
    private MateriaPrima materia;
    private TipoMovimentoMP tipo;
    private BigDecimal quantidade;
    private String observacao;

    public MovimentoStockMP() {}

    public MovimentoStockMP(User user, MateriaPrima materia,
                            TipoMovimentoMP tipo, BigDecimal quantidade,
                            String observacao) {
        this.user       = user;
        this.materia    = materia;
        this.tipo       = tipo;
        this.quantidade = quantidade;
        this.observacao = observacao;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public User             getUser()                       { return user; }
    public void             setUser(User user)              { this.user = user; }

    public MateriaPrima     getMateria()                    { return materia; }
    public void             setMateria(MateriaPrima m)      { this.materia = m; }

    public TipoMovimentoMP  getTipo()                       { return tipo; }
    public void             setTipo(TipoMovimentoMP tipo)   { this.tipo = tipo; }

    public BigDecimal       getQuantidade()                 { return quantidade; }
    public void             setQuantidade(BigDecimal q)     { this.quantidade = q; }

    public String           getObservacao()                 { return observacao; }
    public void             setObservacao(String obs)       { this.observacao = obs; }
}
