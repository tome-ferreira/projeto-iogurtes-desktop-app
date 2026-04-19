//v1.0
package com.gestaoiogurtes.bll.model.enums;

/**
 * Estado da relação entre uma encomenda e uma ordem de produção.
 */
public enum EstadoEncomendaOrdem {
    /** Ordem ainda não produzida. */
    pendente,
    /** Produção concluída. */
    produzido,
    /** Produto expedido. */
    expedido
}
