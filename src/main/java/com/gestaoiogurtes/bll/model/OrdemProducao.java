//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.EstadoOrdem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Ordem de produção: define o que produzir, quando, e
 * regista os consumos de matérias-primas associados.
 */
public class OrdemProducao extends BaseEntity {

    private EstadoOrdem estado;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private User user;
    private LocalDateTime aprovadoEm;
    private String observacoes;
    private List<OrdemProducaoProduto> produtos  = new ArrayList<>();
    private List<ConsumoProducao>      consumos  = new ArrayList<>();

    public OrdemProducao() {}

    public OrdemProducao(User user, LocalDateTime dataInicio,
                         LocalDateTime dataFim, String observacoes) {
        this.user        = user;
        this.dataInicio  = dataInicio;
        this.dataFim     = dataFim;
        this.observacoes = observacoes;
        this.estado      = EstadoOrdem.EM_PRODUCAO;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public EstadoOrdem   getEstado()                             { return estado; }
    public void          setEstado(EstadoOrdem estado)           { this.estado = estado; }

    public LocalDateTime getDataInicio()                         { return dataInicio; }
    public void          setDataInicio(LocalDateTime d)          { this.dataInicio = d; }

    public LocalDateTime getDataFim()                            { return dataFim; }
    public void          setDataFim(LocalDateTime d)             { this.dataFim = d; }

    public User          getUser()                               { return user; }
    public void          setUser(User user)                      { this.user = user; }

    public LocalDateTime getAprovadoEm()                        { return aprovadoEm; }
    public void          setAprovadoEm(LocalDateTime d)         { this.aprovadoEm = d; }

    public String        getObservacoes()                        { return observacoes; }
    public void          setObservacoes(String obs)              { this.observacoes = obs; }

    public List<OrdemProducaoProduto> getProdutos()              { return produtos; }
    public void          setProdutos(List<OrdemProducaoProduto> p){ this.produtos = p; }

    public List<ConsumoProducao>      getConsumos()              { return consumos; }
    public void          setConsumos(List<ConsumoProducao> c)    { this.consumos = c; }
}
