package com.gestaoiogurtes.models.tipoMateriaPrima;

import java.time.LocalDateTime;
import java.util.UUID;

public class TipoMateriaPrimaResponse {
    public UUID id;
    public String nome;
    public String descricao;
    public Double iva;
    public Boolean isActive;
    public LocalDateTime createdAt;

    public TipoMateriaPrimaResponse() {}
}
