//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.EstadoEncomendaOrdem;

/**
 * Relação entre uma encomenda (linha de pallet) e uma ordem de produção.
 */
public class EncomendaOrdem extends BaseEntity {

    private OrdemProducao ordem;
    private EncomendaPallet encomendaPallet;
    private Integer quantidadePallets;
    private EstadoEncomendaOrdem estado;

    public EncomendaOrdem() {}

    public EncomendaOrdem(OrdemProducao ordem, EncomendaPallet encomendaPallet,
                          Integer quantidadePallets) {
        this.ordem             = ordem;
        this.encomendaPallet   = encomendaPallet;
        this.quantidadePallets = quantidadePallets;
        this.estado            = EstadoEncomendaOrdem.pendente;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public OrdemProducao          getOrdem()                        { return ordem; }
    public void                   setOrdem(OrdemProducao o)         { this.ordem = o; }

    public EncomendaPallet         getEncomendaPallet()              { return encomendaPallet; }
    public void                   setEncomendaPallet(EncomendaPallet e){ this.encomendaPallet = e; }

    public Integer                getQuantidadePallets()            { return quantidadePallets; }
    public void                   setQuantidadePallets(Integer q)   { this.quantidadePallets = q; }

    public EstadoEncomendaOrdem   getEstado()                       { return estado; }
    public void                   setEstado(EstadoEncomendaOrdem e) { this.estado = e; }
}
