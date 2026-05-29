package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

/**
 * DTO de resposta para GET /materias-primas/{materiaId}/fornecedores.
 * Campos == chaves JSON exactas (MateriaFornecedorResponse da API).
 */
public class MateriaPrimaFornecedorResponse {
    public UUID    id;
    public UUID    materiaId;
    public String  materiaNome;
    public UUID    fornecedorId;
    public String  fornecedorNome;
    public UUID    moedaId;
    public String  moedaCodigo;
    public String  moedaSimbolo;
    public Double  precoUnitario;
    public Integer prazoEstimadoEntregaDias;
    public Boolean preferencial;
    public Boolean isActive;
    public String  createdAt;
    public String  updatedAt;

    public MateriaPrimaFornecedorResponse() {}
}
