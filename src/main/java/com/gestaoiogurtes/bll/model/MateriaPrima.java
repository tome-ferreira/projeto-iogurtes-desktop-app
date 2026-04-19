//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.TipoMateriaPrima;

import java.math.BigDecimal;

/**
 * Matéria-prima usada na produção de iogurtes (leite, açúcar, aromas, etc.).
 */
public class MateriaPrima extends BaseEntity {

    private String nome;
    private TipoMateriaPrima tipo;
    private String unidade;
    private BigDecimal stockAtual;
    private BigDecimal stockMinimo;
    private BigDecimal precoUnitario;
    private Fornecedor fornecedor;

    public MateriaPrima() {}

    public MateriaPrima(String nome, TipoMateriaPrima tipo, String unidade,
                        BigDecimal stockAtual, BigDecimal stockMinimo,
                        BigDecimal precoUnitario, Fornecedor fornecedor) {
        this.nome          = nome;
        this.tipo          = tipo;
        this.unidade       = unidade;
        this.stockAtual    = stockAtual;
        this.stockMinimo   = stockMinimo;
        this.precoUnitario = precoUnitario;
        this.fornecedor    = fornecedor;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public String          getNome()                         { return nome; }
    public void            setNome(String nome)              { this.nome = nome; }

    public TipoMateriaPrima getTipo()                        { return tipo; }
    public void            setTipo(TipoMateriaPrima tipo)    { this.tipo = tipo; }

    public String          getUnidade()                      { return unidade; }
    public void            setUnidade(String unidade)        { this.unidade = unidade; }

    public BigDecimal      getStockAtual()                   { return stockAtual; }
    public void            setStockAtual(BigDecimal stock)   { this.stockAtual = stock; }

    public BigDecimal      getStockMinimo()                  { return stockMinimo; }
    public void            setStockMinimo(BigDecimal min)    { this.stockMinimo = min; }

    public BigDecimal      getPrecoUnitario()                { return precoUnitario; }
    public void            setPrecoUnitario(BigDecimal preco){ this.precoUnitario = preco; }

    public Fornecedor      getFornecedor()                   { return fornecedor; }
    public void            setFornecedor(Fornecedor f)       { this.fornecedor = f; }
}
