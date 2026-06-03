package com.gestaoiogurtes.models.encomenda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EncomendaDetalheResponse {
    public UUID id;
    public UUID userId;
    public String userNome;
    public UUID moedaId;
    public String moedaCodigo;
    public String moedaSimbolo;
    public Double taxaConversaoSnapshot;
    public String estado;
    public LocalDateTime dataEncomenda;
    public Double totalPreco;
    public Double totalPrecoEur;
    public List<EncomendaPalletResponse> pallets;
    public Boolean isActive;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public EncomendaDetalheResponse() {}
}
