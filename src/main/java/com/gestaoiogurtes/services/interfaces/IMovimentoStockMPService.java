//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.MovimentoStockMP;
import com.gestaoiogurtes.bll.model.enums.TipoMovimentoMP;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code MovimentoStockMPService} do BLL Spring Boot.
 * <p>
 * O método {@link #registarMovimento} é transaccional no servidor real; no mock
 * é executado em memória de forma síncrona.
 * </p>
 */
public interface IMovimentoStockMPService {

    /**
     * Regista um movimento de stock de matéria-prima e actualiza o {@code stockAtual}.
     * <ul>
     *   <li>{@code ENTRADA} → {@code stockAtual += quantidade}</li>
     *   <li>{@code SAIDA}   → {@code stockAtual -= quantidade}</li>
     *   <li>{@code AJUSTE}  → {@code stockAtual =  quantidade}</li>
     * </ul>
     *
     * @param userId     UUID do utilizador que registou o movimento
     * @param materiaId  UUID da matéria-prima afectada
     * @param tipo       tipo de movimento {@link TipoMovimentoMP}
     * @param quantidade quantidade movimentada; deve ser positiva
     * @param observacao nota livre; pode ser {@code null}
     * @return {@link MovimentoStockMP} persistido
     * @throws IllegalArgumentException com mensagem {@code "Matéria prima não encontrada!"} se não existir
     */
    MovimentoStockMP registarMovimento(UUID userId, UUID materiaId,
                                       TipoMovimentoMP tipo, BigDecimal quantidade,
                                       String observacao);

    /**
     * Retorna todos os movimentos de uma matéria-prima (incluindo inactivos).
     *
     * @param materiaId UUID da matéria-prima
     * @return lista de movimentos
     */
    List<MovimentoStockMP> getByMateria(UUID materiaId);

    /**
     * Retorna todos os movimentos registados por um utilizador.
     *
     * @param userId UUID do utilizador
     * @return lista de movimentos
     */
    List<MovimentoStockMP> getByUser(UUID userId);

    /**
     * Retorna todos os movimentos activos.
     *
     * @return lista de movimentos activos
     */
    List<MovimentoStockMP> getAll();

    /**
     * Retorna todos os movimentos, incluindo os eliminados.
     *
     * @return lista completa de movimentos
     */
    List<MovimentoStockMP> getAllIncludingInactive();
}
