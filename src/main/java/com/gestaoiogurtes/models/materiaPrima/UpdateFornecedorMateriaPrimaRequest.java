package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

/**
 * Corpo do PUT /materias-primas/fornecedores/{id}.
 * Campos == chaves JSON exactas (UpdateMateriaFornecedorRequest da API).
 */
public class UpdateFornecedorMateriaPrimaRequest {
    public UUID    moedaId;
    public Double  precoUnitario;
    public Integer prazoEstimadoEntregaDias;
    public Boolean preferencial;

    public UpdateFornecedorMateriaPrimaRequest() {}
}
