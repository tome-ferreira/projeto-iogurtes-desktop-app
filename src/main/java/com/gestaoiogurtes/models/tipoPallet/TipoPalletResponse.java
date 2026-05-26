package com.gestaoiogurtes.models.tipoPallet;

import java.time.LocalDateTime;
import java.util.UUID;

public class TipoPalletResponse {
    public UUID id;
    public String nome;
    public Double capacidadeKg;
    public Boolean isActive;
    public LocalDateTime createdAt;

    public TipoPalletResponse() {}
}
