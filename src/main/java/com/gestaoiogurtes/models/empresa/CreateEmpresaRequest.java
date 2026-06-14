package com.gestaoiogurtes.models.empresa;

public class CreateEmpresaRequest {

    public String nomeEmpresa;
    public String nipc;
    public String telefone;
    public String morada;
    public String codigoPostal;
    public String cidade;

    public CreateEmpresaRequest(String nomeEmpresa, String nipc, String telefone,
            String morada, String codigoPostal, String cidade) {
        this.nomeEmpresa = nomeEmpresa;
        this.nipc = nipc;
        this.telefone = telefone;
        this.morada = morada;
        this.codigoPostal = codigoPostal;
        this.cidade = cidade;
    }
}
