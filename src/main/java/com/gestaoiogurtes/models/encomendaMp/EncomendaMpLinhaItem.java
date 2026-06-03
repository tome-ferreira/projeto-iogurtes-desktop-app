package com.gestaoiogurtes.models.encomendaMp;

import java.util.UUID;

/**
 * Um item na lista de linhas do pedido de criação de encomenda MP.
 * Campos == chaves JSON exactas (CreateEncomendaMPLinhaRequest da API).
 */
public class EncomendaMpLinhaItem {
    /** UUID da matéria prima a encomendar. */
    public UUID   materiaId;
    /** Quantidade a encomendar (mínimo 0.001). */
    public Double quantidade;

    public EncomendaMpLinhaItem() {}

    public EncomendaMpLinhaItem(UUID materiaId, Double quantidade) {
        this.materiaId  = materiaId;
        this.quantidade = quantidade;
    }
}
