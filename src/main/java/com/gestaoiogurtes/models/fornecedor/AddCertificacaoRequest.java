package com.gestaoiogurtes.models.fornecedor;

/**
 * Corpo do POST /fornecedor/{fornecedorId}/certificacoes.
 * Campos coincidem exactamente com as chaves JSON da API.
 */
public class AddCertificacaoRequest {
    public String certificacaoId;
    /** Formato "yyyy-MM-dd". */
    public String dataInicio;
    /** Formato "yyyy-MM-dd". */
    public String dataFim;

    public AddCertificacaoRequest(String certificacaoId, String dataInicio, String dataFim) {
        this.certificacaoId = certificacaoId;
        this.dataInicio     = dataInicio;
        this.dataFim        = dataFim;
    }
}
