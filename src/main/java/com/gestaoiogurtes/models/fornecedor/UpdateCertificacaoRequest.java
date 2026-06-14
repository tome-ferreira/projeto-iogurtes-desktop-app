package com.gestaoiogurtes.models.fornecedor;

public class UpdateCertificacaoRequest {
    public String dataInicio;
    public String dataFim;

    public UpdateCertificacaoRequest(String dataInicio, String dataFim) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }
}
