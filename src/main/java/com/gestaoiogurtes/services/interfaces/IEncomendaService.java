//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.Encomenda;
import com.gestaoiogurtes.bll.model.EncomendaPallet;

import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code EncomendaService} do BLL Spring Boot.
 */
public interface IEncomendaService {

    /**
     * Cria uma encomenda com lógica automática de stock:
     * <ol>
     *   <li>Calcula {@code totalPreco} automaticamente.</li>
     *   <li>Para pallets com stock suficiente → regista expedição imediatamente.</li>
     *   <li>Para pallets sem stock → cria {@code OrdemProducao} em {@code AGUARDA_APROVACAO}.</li>
     *   <li>Se todos tiverem stock → estado final {@code confirmada}; caso contrário → {@code pendente}.</li>
     * </ol>
     *
     * @param userId  UUID do utilizador que cria a encomenda
     * @param pallets lista de linhas de pallet; cada {@link EncomendaPallet} deve ter
     *                {@code produto}, {@code palletTipo}, {@code quantidadePallets}
     *                e {@code precoPorPallet} definidos
     * @return {@link Encomenda} criada
     * @throws IllegalArgumentException se utilizador, produto ou tipo de pallet não existirem
     */
    Encomenda createEncomenda(UUID userId, List<EncomendaPallet> pallets);

    /**
     * Obtém uma encomenda pelo seu identificador único.
     *
     * @param id UUID da encomenda
     * @return {@link Encomenda} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Encomenda não encontrada"}
     */
    Encomenda getById(UUID id);

    /**
     * Retorna todas as encomendas activas.
     *
     * @return lista de encomendas activas; pode ser vazia
     */
    List<Encomenda> getAll();

    /**
     * Retorna todos os registos de encomendas, incluindo os eliminados.
     *
     * @return lista completa
     */
    List<Encomenda> getAllIncludingInactive();

    /**
     * Executa soft-delete em cascata: {@code EncomendaPallet}, {@code EncomendaOrdem}
     * e ordens de produção associadas.
     *
     * @param id UUID da encomenda
     * @throws IllegalArgumentException com mensagem {@code "Encomenda não encontrada"}
     */
    void delete(UUID id);
}
