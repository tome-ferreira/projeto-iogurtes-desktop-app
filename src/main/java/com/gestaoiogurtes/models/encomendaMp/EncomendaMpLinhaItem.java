package com.gestaoiogurtes.models.encomendaMp;

import java.util.UUID;

public class EncomendaMpLinhaItem {

    public UUID materiaId;
    public Double quantidade;

    public EncomendaMpLinhaItem() {
    }

    public EncomendaMpLinhaItem(UUID materiaId, Double quantidade) {
        this.materiaId = materiaId;
        this.quantidade = quantidade;
    }
}
