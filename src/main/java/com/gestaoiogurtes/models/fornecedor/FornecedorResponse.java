package com.gestaoiogurtes.models.fornecedor;

import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FornecedorResponse {
    public UUID id;
    public String nome;
    public String nif;
    public String email;
    public String telefone;
    public String morada;
    public String cidade;
    public FornecedorTipoResponse tipo;
    public List<Object> certificacoes;
    public Boolean isActive;
    public LocalDateTime createdAt;

    public FornecedorResponse() {}
}
