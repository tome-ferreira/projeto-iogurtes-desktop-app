//v1.0
package com.gestaoiogurtes.bll.model;

import java.math.BigDecimal;

/**
 * Tipo de pallet utilizado nas encomendas.
 */
public class PalletTipo extends BaseEntity {

    private String nome;
    private BigDecimal capacidadeKg;

    public PalletTipo() {}

    public PalletTipo(String nome, BigDecimal capacidadeKg) {
        this.nome        = nome;
        this.capacidadeKg = capacidadeKg;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public String     getNome()                           { return nome; }
    public void       setNome(String nome)                { this.nome = nome; }

    public BigDecimal getCapacidadeKg()                   { return capacidadeKg; }
    public void       setCapacidadeKg(BigDecimal cap)     { this.capacidadeKg = cap; }
}
