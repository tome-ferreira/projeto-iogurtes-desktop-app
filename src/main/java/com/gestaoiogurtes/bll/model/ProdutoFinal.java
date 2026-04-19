//v1.0
package com.gestaoiogurtes.bll.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Produto final (iogurte) com ingredientes, preços e stock.
 */
public class ProdutoFinal extends BaseEntity {

    private String codigoSku;
    private String nome;
    private String descricao;
    private Integer validadeDias;
    private BigDecimal precoVenda;
    private BigDecimal precoPorKg;
    private Boolean visivelCliente;
    private Integer stockAtual;
    private Integer quantidadeLote;
    private List<ProdutoMateria> materias = new ArrayList<>();

    public ProdutoFinal() {}

    public ProdutoFinal(String codigoSku, String nome, String descricao,
                        Integer validadeDias, BigDecimal precoVenda,
                        BigDecimal precoPorKg, Integer quantidadeLote) {
        this.codigoSku     = codigoSku;
        this.nome          = nome;
        this.descricao     = descricao;
        this.validadeDias  = validadeDias;
        this.precoVenda    = precoVenda;
        this.precoPorKg    = precoPorKg;
        this.quantidadeLote = quantidadeLote;
        this.stockAtual    = 0;
        this.visivelCliente = false;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public String          getCodigoSku()                        { return codigoSku; }
    public void            setCodigoSku(String sku)              { this.codigoSku = sku; }

    public String          getNome()                             { return nome; }
    public void            setNome(String nome)                  { this.nome = nome; }

    public String          getDescricao()                        { return descricao; }
    public void            setDescricao(String descricao)        { this.descricao = descricao; }

    public Integer         getValidadeDias()                     { return validadeDias; }
    public void            setValidadeDias(Integer dias)         { this.validadeDias = dias; }

    public BigDecimal      getPrecoVenda()                       { return precoVenda; }
    public void            setPrecoVenda(BigDecimal preco)       { this.precoVenda = preco; }

    public BigDecimal      getPrecoPorKg()                       { return precoPorKg; }
    public void            setPrecoPorKg(BigDecimal preco)       { this.precoPorKg = preco; }

    public Boolean         getVisivelCliente()                   { return visivelCliente; }
    public void            setVisivelCliente(Boolean v)          { this.visivelCliente = v; }

    public Integer         getStockAtual()                       { return stockAtual; }
    public void            setStockAtual(Integer stock)          { this.stockAtual = stock; }

    public Integer         getQuantidadeLote()                   { return quantidadeLote; }
    public void            setQuantidadeLote(Integer lote)       { this.quantidadeLote = lote; }

    public List<ProdutoMateria> getMaterias()                    { return materias; }
    public void            setMaterias(List<ProdutoMateria> m)   { this.materias = m; }
}
