package com.gestaoiogurtes.models.encomendaMp;

import java.util.List;
import java.util.UUID;

public class EncomendaMpResponse {
    public UUID id;
    public UUID userId;
    public String userNome;
    public UUID fornecedorId;
    public String fornecedorNome;
    public UUID moedaId;
    public String moedaCodigo;
    public String moedaSimbolo;
    public Double taxaConversaoSnapshot;
    public String estado;
    public String dataEncomenda;
    public String dataEntregaPrevista;
    public Double totalPrecoSemIva;
    public Double totalPrecoEurSemIva;
    public Double totalPrecoEurComIva;
    public String observacoes;
    public List<EncomendaMpLinhaResponse> linhas;
    public Boolean isActive;
    public String createdAt;
    public String updatedAt;

    public EncomendaMpResponse() {
    }
}
