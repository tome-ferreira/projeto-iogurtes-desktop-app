//v1.0
package com.gestaoiogurtes.bll.model.enums;

/**
 * Estado de uma encomenda.
 * <p>ATENÇÃO: os valores usam minúsculas — conforme contrato § 7.9.</p>
 */
public enum EstadoEncomenda {
    /** Encomenda criada mas com stock insuficiente; aguarda ordens de produção. */
    pendente,
    /** Stock total disponível; encomenda expedida imediatamente. */
    confirmada,
    /** Mercadoria enviada. */
    expedida,
    /** Encomenda recebida pelo cliente. */
    entregue,
    /** Encomenda cancelada. */
    cancelada
}
