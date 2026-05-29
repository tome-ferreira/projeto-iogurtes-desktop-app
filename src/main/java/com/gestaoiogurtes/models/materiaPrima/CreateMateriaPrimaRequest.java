package com.gestaoiogurtes.models.materiaPrima;

import java.util.UUID;

public class CreateMateriaPrimaRequest {
    public String nome;
    public String unidade;
    public Double stockMinimo;
    public UUID tipoId;

    public CreateMateriaPrimaRequest() {}
}
