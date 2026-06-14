package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

public class AddFornecedorMateriaPrimaRequest {
    public UUID fornecedorId;
    public UUID moedaId;
    public Double precoUnitario;
    public Integer prazoEstimadoEntregaDias;
    public Boolean preferencial;

    public AddFornecedorMateriaPrimaRequest() {
    }
}
