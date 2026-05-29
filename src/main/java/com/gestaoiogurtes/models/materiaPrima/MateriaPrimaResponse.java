package com.gestaoiogurtes.models.materiaPrima;

import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public class MateriaPrimaResponse {
    public UUID id;
    public String nome;
    public String unidade;
    public Double stockAtual;
    public Double stockMinimo;
    public TipoMateriaPrimaResponse tipo;
    public Boolean isActive;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public MateriaPrimaResponse() {}
}
