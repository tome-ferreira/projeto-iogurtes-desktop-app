package com.gestaoiogurtes.models.fornecedortipo;

import java.time.LocalDateTime;
import java.util.UUID;

public class FornecedorTipoResponse {
    public UUID id;
    public String nome;
    public String descricao;
    public Boolean isActive;
    public LocalDateTime createdAt;

    public FornecedorTipoResponse() {}
}
