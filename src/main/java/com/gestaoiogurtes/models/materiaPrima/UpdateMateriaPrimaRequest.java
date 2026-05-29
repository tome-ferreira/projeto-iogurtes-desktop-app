package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

public class UpdateMateriaPrimaRequest {
    public String nome;
    public String unidade;
    public Double stockMinimo;
    public UUID tipoId;

    public UpdateMateriaPrimaRequest() {}
}
