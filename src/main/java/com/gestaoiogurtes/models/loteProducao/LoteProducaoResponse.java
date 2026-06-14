package com.gestaoiogurtes.models.loteProducao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LoteProducaoResponse {
    public UUID id;
    public UUID ordemId;
    public UUID produtoId;
    public String produtoNome;
    public String numeroLote;
    public Double quantidadeKg;
    public Double stockAtualKg;
    public String estado;
    public LocalDate dataProducao;
    public LocalDate dataValidade;
    public Boolean isActive;
    public LocalDateTime createdAt;

    public LoteProducaoResponse() {}
}
