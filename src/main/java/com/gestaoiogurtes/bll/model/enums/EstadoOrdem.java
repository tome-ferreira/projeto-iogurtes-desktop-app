//v1.0
package com.gestaoiogurtes.bll.model.enums;

/**
 * Estado de uma ordem de produção.
 * <p>ATENÇÃO: os valores usam MAIÚSCULAS — conforme contrato § 7.9.</p>
 */
public enum EstadoOrdem {
    /** Criada automaticamente por encomenda; aguarda aprovação manual. */
    AGUARDA_APROVACAO,
    /** Em curso (estado default na criação manual). */
    EM_PRODUCAO,
    /** Produção finalizada. */
    CONCLUIDA,
    /** Ordem cancelada; consumos de MP revertidos. */
    CANCELADA
}
