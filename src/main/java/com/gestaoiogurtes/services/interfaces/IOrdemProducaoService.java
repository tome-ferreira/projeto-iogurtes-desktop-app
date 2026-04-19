//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.OrdemProducao;
import com.gestaoiogurtes.bll.model.OrdemProducaoProduto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code OrdemProducaoService} do BLL Spring Boot.
 */
public interface IOrdemProducaoService {

    /**
     * Cria uma ordem de produção, calcula consumos de matérias-primas e
     * regista movimentos {@code SAIDA} em cada ingrediente.
     * <p>
     * Estado inicial: {@code EM_PRODUCAO}.
     * </p>
     *
     * @param userId     UUID do utilizador que cria a ordem
     * @param dataInicio data/hora de início
     * @param dataFim    data/hora de conclusão prevista
     * @param observacoes notas livres; pode ser {@code null}
     * @param produtos   lista de linhas de produto; cada {@link OrdemProducaoProduto}
     *                   deve ter {@code produto} com ID válido e {@code quantidadeKg}
     * @return {@link OrdemProducao} criada
     * @throws IllegalArgumentException se utilizador não encontrado ou validação falhar
     */
    OrdemProducao createOrdem(UUID userId, LocalDateTime dataInicio,
                              LocalDateTime dataFim, String observacoes,
                              List<OrdemProducaoProduto> produtos);

    /**
     * Obtém uma ordem de produção pelo seu identificador único.
     *
     * @param id UUID da ordem
     * @return {@link OrdemProducao} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Ordem não encontrada"}
     */
    OrdemProducao getById(UUID id);

    /**
     * Retorna todas as ordens de produção activas.
     *
     * @return lista de ordens activas; pode ser vazia
     */
    List<OrdemProducao> getAll();

    /**
     * Retorna todos os registos de ordens, incluindo os eliminados.
     *
     * @return lista completa
     */
    List<OrdemProducao> getAllIncludingInactive();

    /**
     * Actualiza campos de data e observações de uma ordem. Campos {@code null} são ignorados.
     *
     * @param id         UUID da ordem
     * @param dataInicio nova data de início; {@code null} mantém o valor actual
     * @param dataFim    nova data de fim; {@code null} mantém o valor actual
     * @param observacoes novas observações; {@code null} mantém o valor actual
     * @return {@link OrdemProducao} actualizada
     * @throws IllegalArgumentException se validação falhar
     */
    OrdemProducao updateOrdem(UUID id, LocalDateTime dataInicio, LocalDateTime dataFim,
                              String observacoes);

    /**
     * Cancela uma ordem e reverte todos os consumos de matérias-primas via movimentos {@code ENTRADA}.
     *
     * @param id     UUID da ordem a cancelar
     * @param userId UUID do utilizador que cancela
     * @return {@link OrdemProducao} no estado {@code CANCELADA}
     * @throws IllegalArgumentException se ordem não encontrada
     */
    OrdemProducao cancelarOrdem(UUID id, UUID userId);

    /**
     * Aprova uma ordem em estado {@code AGUARDA_APROVACAO}, calcula consumos
     * e muda estado para {@code EM_PRODUCAO}.
     *
     * @param ordemId UUID da ordem a aprovar
     * @return {@link OrdemProducao} aprovada
     * @throws IllegalStateException com mensagem {@code "Ordem não está em estado de aprovação"}
     *         se o estado for diferente de {@code AGUARDA_APROVACAO}
     */
    OrdemProducao aprovarOrdem(UUID ordemId);

    /**
     * Executa soft-delete em cascata: produtos da ordem, consumos,
     * {@code EncomendaOrdem} e {@code MovimentoStockPF} associados.
     *
     * @param id UUID da ordem
     * @throws IllegalArgumentException com mensagem {@code "Ordem não encontrada"}
     */
    void delete(UUID id);
}
