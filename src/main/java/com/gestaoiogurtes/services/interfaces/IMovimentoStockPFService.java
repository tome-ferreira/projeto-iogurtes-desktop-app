//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.MovimentoStockPF;
import com.gestaoiogurtes.bll.model.OrdemProducao;
import com.gestaoiogurtes.bll.model.ProdutoFinal;
import com.gestaoiogurtes.bll.model.enums.TipoMovimentoPF;

import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code MovimentoStockPFService} do BLL Spring Boot.
 * <p>
 * <b>Atenção (§ 7.6):</b> este serviço recebe <b>objectos completos</b>
 * ({@link ProdutoFinal}, {@link OrdemProducao}), não IDs — ao contrário
 * de {@link IMovimentoStockMPService}.
 * </p>
 */
public interface IMovimentoStockPFService {

    /**
     * Regista um movimento de stock de produto final e actualiza o {@code stockAtual}.
     * <ul>
     *   <li>{@code PRODUCAO} ou {@code DEVOLUCAO} → {@code stockAtual += quantidade}</li>
     *   <li>{@code EXPEDICAO} → {@code stockAtual -= quantidade} (lança excepção se negativo)</li>
     *   <li>{@code AJUSTE} → {@code stockAtual = quantidade}</li>
     * </ul>
     *
     * @param produto    objecto {@link ProdutoFinal} completo (não um ID)
     * @param ordem      objecto {@link OrdemProducao} associado; pode ser {@code null}
     * @param tipo       tipo de movimento {@link TipoMovimentoPF}
     * @param quantidade quantidade em unidades inteiras
     * @param observacao nota livre; pode ser {@code null}
     * @return {@link MovimentoStockPF} persistido
     * @throws IllegalStateException com mensagem {@code "Stock insuficiente para o produto: …"}
     *         em expedição com quantidade insuficiente
     */
    MovimentoStockPF registarMovimento(ProdutoFinal produto, OrdemProducao ordem,
                                       TipoMovimentoPF tipo, Integer quantidade,
                                       String observacao);

    /**
     * Obtém um movimento pelo seu identificador único.
     *
     * @param id UUID do movimento
     * @return {@link MovimentoStockPF} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Movimento não encontrado"}
     */
    MovimentoStockPF getById(UUID id);

    /**
     * Retorna todos os movimentos activos.
     *
     * @return lista de movimentos activos
     */
    List<MovimentoStockPF> getAll();

    /**
     * Retorna todos os movimentos, incluindo os eliminados.
     *
     * @return lista completa
     */
    List<MovimentoStockPF> getAllIncludingInactive();

    /**
     * Retorna todos os movimentos de um produto específico.
     *
     * @param produtoId UUID do produto
     * @return lista de movimentos do produto
     */
    List<MovimentoStockPF> getByProduto(UUID produtoId);

    /**
     * Retorna todos os movimentos de uma ordem de produção específica.
     *
     * @param ordemId UUID da ordem de produção
     * @return lista de movimentos da ordem
     */
    List<MovimentoStockPF> getByOrdem(UUID ordemId);
}
