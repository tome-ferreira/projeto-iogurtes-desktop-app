//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.EstadoEncomenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Encomenda de pallets de produto final por parte de uma empresa cliente.
 */
public class Encomenda extends BaseEntity {

    private User user;
    private EstadoEncomenda estado;
    private LocalDateTime dataEncomenda;
    private BigDecimal totalPreco;
    private List<EncomendaPallet> pallets = new ArrayList<>();

    public Encomenda() {}

    public Encomenda(User user, BigDecimal totalPreco) {
        this.user          = user;
        this.totalPreco    = totalPreco;
        this.dataEncomenda = LocalDateTime.now();
        this.estado        = EstadoEncomenda.pendente;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public User                 getUser()                          { return user; }
    public void                 setUser(User user)                 { this.user = user; }

    public EstadoEncomenda      getEstado()                        { return estado; }
    public void                 setEstado(EstadoEncomenda estado)  { this.estado = estado; }

    public LocalDateTime        getDataEncomenda()                 { return dataEncomenda; }
    public void                 setDataEncomenda(LocalDateTime d)  { this.dataEncomenda = d; }

    public BigDecimal           getTotalPreco()                    { return totalPreco; }
    public void                 setTotalPreco(BigDecimal total)    { this.totalPreco = total; }

    public List<EncomendaPallet> getPallets()                      { return pallets; }
    public void                 setPallets(List<EncomendaPallet> p){ this.pallets = p; }
}
