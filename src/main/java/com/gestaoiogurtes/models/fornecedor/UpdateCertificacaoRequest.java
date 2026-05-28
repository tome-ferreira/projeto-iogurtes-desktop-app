package com.gestaoiogurtes.models.fornecedor;

/**
 * Corpo do PUT /fornecedor/certificacoes/{fornecedorCertificacaoId}.
 * Schema da API: UpdateFornecedorCertificacaoRequest.
 * Campos coincidem exactamente com as chaves JSON da API.
 */
public class UpdateCertificacaoRequest {
    /** Formato "yyyy-MM-dd". */
    public String dataInicio;
    /** Formato "yyyy-MM-dd". */
    public String dataFim;

    public UpdateCertificacaoRequest(String dataInicio, String dataFim) {
        this.dataInicio = dataInicio;
        this.dataFim    = dataFim;
    }
}
