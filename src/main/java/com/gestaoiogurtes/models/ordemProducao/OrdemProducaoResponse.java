package com.gestaoiogurtes.models.ordemProducao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrdemProducaoResponse {
    public UUID id;
    public UUID userId;
    public String userNome;
    public String estado;
    public LocalDateTime dataInicio;
    public LocalDateTime dataFim;
    public LocalDateTime aprovadoEm;
    public String observacoes;
    public List<OrdemProducaoProdutoResponse> produtos;
    public Boolean isActive;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public OrdemProducaoResponse() {
    }
}
