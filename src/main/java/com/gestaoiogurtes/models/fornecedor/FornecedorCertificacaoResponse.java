package com.gestaoiogurtes.models.fornecedor;

import java.util.UUID;

public class FornecedorCertificacaoResponse {
    public UUID id;
    public String nomeFornecedor;
    public String certificacaoNome;
    public String dataInicio;
    public String dataFim;
    public Boolean isActive;

    public FornecedorCertificacaoResponse() {
    }
}
