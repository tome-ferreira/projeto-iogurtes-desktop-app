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
    // We can ignore consumos since it's not requested, but let's map it safely if we need or just omit it. Gson will ignore it if omitted.
    public Boolean isActive;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public OrdemProducaoResponse() {}
}
