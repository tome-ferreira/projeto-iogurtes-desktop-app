package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

/**
 * Corpo do POST /materias-primas/{materiaId}/fornecedores.
 * Campos == chaves JSON exactas (CreateMateriaFornecedorRequest da API).
 */
public class AddFornecedorMateriaPrimaRequest {
    public UUID    fornecedorId;
    public UUID    moedaId;
    public Double  precoUnitario;
    public Integer prazoEstimadoEntregaDias;
    public Boolean preferencial;

    public AddFornecedorMateriaPrimaRequest() {}
}
