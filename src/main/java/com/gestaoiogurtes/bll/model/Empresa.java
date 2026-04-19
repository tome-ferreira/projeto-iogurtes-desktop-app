//v1.0
package com.gestaoiogurtes.bll.model;

/**
 * Empresa cliente do sistema.
 */
public class Empresa extends BaseEntity {

    private String nomeEmpresa;
    private String nipc;
    private String telefone;
    private String morada;
    private String codigoPostal;
    private String cidade;

    public Empresa() {}

    public Empresa(String nomeEmpresa, String nipc, String telefone,
                   String morada, String codigoPostal, String cidade) {
        this.nomeEmpresa  = nomeEmpresa;
        this.nipc         = nipc;
        this.telefone     = telefone;
        this.morada       = morada;
        this.codigoPostal = codigoPostal;
        this.cidade       = cidade;
    }

    // ── Getters / setters ──────────────────────────────────────────

    public String getNomeEmpresa()                     { return nomeEmpresa; }
    public void   setNomeEmpresa(String nomeEmpresa)   { this.nomeEmpresa = nomeEmpresa; }

    public String getNipc()                            { return nipc; }
    public void   setNipc(String nipc)                 { this.nipc = nipc; }

    public String getTelefone()                        { return telefone; }
    public void   setTelefone(String telefone)         { this.telefone = telefone; }

    public String getMorada()                          { return morada; }
    public void   setMorada(String morada)             { this.morada = morada; }

    public String getCodigoPostal()                    { return codigoPostal; }
    public void   setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getCidade()                          { return cidade; }
    public void   setCidade(String cidade)             { this.cidade = cidade; }
}
