//v1.0
package com.gestaoiogurtes.bll.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Fornecedor de matérias-primas.
 */
public class Fornecedor extends BaseEntity {

    private String nome;
    private String nif;
    private String email;
    private String telefone;
    private String morada;
    private List<FornecedorCertificacao> certificacoes = new ArrayList<>();

    public Fornecedor() {}

    public Fornecedor(String nome, String nif, String email,
                      String telefone, String morada) {
        this.nome     = nome;
        this.nif      = nif;
        this.email    = email;
        this.telefone = telefone;
        this.morada   = morada;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public String getNome()                                              { return nome; }
    public void   setNome(String nome)                                   { this.nome = nome; }

    public String getNif()                                               { return nif; }
    public void   setNif(String nif)                                     { this.nif = nif; }

    public String getEmail()                                             { return email; }
    public void   setEmail(String email)                                 { this.email = email; }

    public String getTelefone()                                          { return telefone; }
    public void   setTelefone(String telefone)                           { this.telefone = telefone; }

    public String getMorada()                                            { return morada; }
    public void   setMorada(String morada)                               { this.morada = morada; }

    public List<FornecedorCertificacao> getCertificacoes()               { return certificacoes; }
    public void setCertificacoes(List<FornecedorCertificacao> certs)     { this.certificacoes = certs; }
}
