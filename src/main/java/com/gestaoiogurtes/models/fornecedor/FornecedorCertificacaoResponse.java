package com.gestaoiogurtes.models.fornecedor;

import java.util.UUID;

/**
 * DTO de resposta para FornecedorCertificacao.
 * Campos coincidem exactamente com as chaves JSON da API.
 */
public class FornecedorCertificacaoResponse {
    public UUID    id;
    public String  nomeFornecedor;
    public String  certificacaoNome;
    /** Data no formato "yyyy-MM-dd" (API retorna date, não datetime). */
    public String  dataInicio;
    public String  dataFim;
    public Boolean isActive;

    public FornecedorCertificacaoResponse() {}
}
