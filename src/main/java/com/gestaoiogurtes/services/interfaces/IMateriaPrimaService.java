//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.MateriaPrima;
import com.gestaoiogurtes.bll.model.enums.TipoMateriaPrima;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code MateriaPrimaService} do BLL Spring Boot.
 */
public interface IMateriaPrimaService {

    /**
     * Cria e persiste uma nova matéria-prima.
     *
     * @param nome           nome da matéria-prima; obrigatório
     * @param unidade        unidade de medida (ex: {@code "kg"}, {@code "L"})
     * @param tipo           categoria {@link TipoMateriaPrima}
     * @param stockAtual     stock actual em unidades
     * @param stockMinimo    stock mínimo de alerta
     * @param precoUnitario  preço por unidade
     * @param fornecedorId   UUID do fornecedor associado; obrigatório
     * @return {@link MateriaPrima} persistida
     * @throws IllegalArgumentException se validação falhar
     */
    MateriaPrima createMateriaPrima(String nome, String unidade, TipoMateriaPrima tipo,
                                    BigDecimal stockAtual, BigDecimal stockMinimo,
                                    BigDecimal precoUnitario, UUID fornecedorId);

    /**
     * Actualiza uma matéria-prima existente.
     * <p>
     * <b>Nota:</b> {@code stockAtual} não é actualizável por este método;
     * usa-se {@link IMovimentoStockMPService#registarMovimento}.
     * </p>
     *
     * @param id             UUID da matéria-prima
     * @param nome           novo nome
     * @param unidade        nova unidade
     * @param tipo           novo tipo
     * @param stockMinimo    novo stock mínimo
     * @param precoUnitario  novo preço unitário
     * @param fornecedorId   novo UUID do fornecedor
     * @return {@link MateriaPrima} actualizada
     * @throws IllegalArgumentException com mensagem {@code "Matéria prima não encontrada"}
     */
    MateriaPrima updateMateriaPrima(UUID id, String nome, String unidade, TipoMateriaPrima tipo,
                                    BigDecimal stockMinimo, BigDecimal precoUnitario,
                                    UUID fornecedorId);

    /**
     * Obtém uma matéria-prima pelo seu identificador único.
     *
     * @param id UUID da matéria-prima
     * @return {@link MateriaPrima} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Matéria prima não encontrada"}
     */
    MateriaPrima getById(UUID id);

    /**
     * Retorna todas as matérias-primas activas.
     *
     * @return lista de matérias-primas activas; pode ser vazia
     */
    List<MateriaPrima> getAll();

    /**
     * Retorna todos os registos de matérias-primas, incluindo os eliminados.
     *
     * @return lista completa
     */
    List<MateriaPrima> getAllIncludingInactive();

    /**
     * Executa soft-delete em cascata: {@code ProdutoMateria} e {@code MovimentoStockMP} relacionados.
     *
     * @param id UUID da matéria-prima
     * @throws IllegalArgumentException com mensagem {@code "Matéria prima não encontrada"}
     */
    void delete(UUID id);
}
