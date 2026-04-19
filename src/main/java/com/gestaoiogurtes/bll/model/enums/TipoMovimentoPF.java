//v1.0
package com.gestaoiogurtes.bll.model.enums;

/**
 * Tipo de movimento de stock de produto final.
 */
public enum TipoMovimentoPF {
    /** Produto acabado de produzir; adiciona ao stock. */
    PRODUCAO,
    /** Produto expedido; subtrai do stock. */
    EXPEDICAO,
    /** Ajuste manual; define o stock directamente. */
    AJUSTE,
    /** Devolução de cliente; adiciona ao stock. */
    DEVOLUCAO
}
