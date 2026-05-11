package com.gestaoiogurtes.model;

/**
 * Corpo do pedido PUT /empresas/{id} (actualização de uma empresa existente).
 *
 * <p>Campos JSON (UpdateEmpresaRequest):</p>
 * <pre>
 * {
 *   "nomeEmpresa":  "string",
 *   "nipc":         "string",
 *   "telefone":     "string",
 *   "morada":       "string",
 *   "codigoPostal": "string",
 *   "cidade":       "string"
 * }
 * </pre>
 */
public class UpdateEmpresaRequest {

    public String nomeEmpresa;
    public String nipc;
    public String telefone;
    public String morada;
    public String codigoPostal;
    public String cidade;

    public UpdateEmpresaRequest(String nomeEmpresa, String nipc, String telefone,
                                String morada, String codigoPostal, String cidade) {
        this.nomeEmpresa  = nomeEmpresa;
        this.nipc         = nipc;
        this.telefone     = telefone;
        this.morada       = morada;
        this.codigoPostal = codigoPostal;
        this.cidade       = cidade;
    }
}
