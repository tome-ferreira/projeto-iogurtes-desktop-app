package com.gestaoiogurtes.models.produtoFinal;

import java.util.List;

public class AddMateriasComposicaoRequest {
    public List<CreateProdutoMateriaRequest> materias;

    public AddMateriasComposicaoRequest() {}

    public AddMateriasComposicaoRequest(List<CreateProdutoMateriaRequest> materias) {
        this.materias = materias;
    }
}
