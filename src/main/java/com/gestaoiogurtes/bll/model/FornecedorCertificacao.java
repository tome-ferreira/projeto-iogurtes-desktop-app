//v1.0
package com.gestaoiogurtes.bll.model;

import com.gestaoiogurtes.bll.model.enums.TipoCertificacao;

import java.time.LocalDate;

/**
 * Certificação associada a um fornecedor.
 */
public class FornecedorCertificacao extends BaseEntity {

    private Fornecedor fornecedor;
    private TipoCertificacao tipo;
    private String descricao;
    private LocalDate validade;

    public FornecedorCertificacao() {}

    // ── Getters / setters ──────────────────────────────────────────

    public Fornecedor        getFornecedor()               { return fornecedor; }
    public void              setFornecedor(Fornecedor f)   { this.fornecedor = f; }

    public TipoCertificacao  getTipo()                     { return tipo; }
    public void              setTipo(TipoCertificacao t)   { this.tipo = t; }

    public String            getDescricao()                { return descricao; }
    public void              setDescricao(String d)        { this.descricao = d; }

    public LocalDate         getValidade()                 { return validade; }
    public void              setValidade(LocalDate v)      { this.validade = v; }
}
