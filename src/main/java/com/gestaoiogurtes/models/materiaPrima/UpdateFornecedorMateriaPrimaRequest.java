package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

public class UpdateFornecedorMateriaPrimaRequest {
    public UUID moedaId;
    public Double precoUnitario;
    public Integer prazoEstimadoEntregaDias;
    public Boolean preferencial;

    public UpdateFornecedorMateriaPrimaRequest() {
    }
}
