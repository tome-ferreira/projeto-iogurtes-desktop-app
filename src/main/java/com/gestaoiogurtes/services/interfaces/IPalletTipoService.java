//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.PalletTipo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code PalletTipoService} do BLL Spring Boot.
 */
public interface IPalletTipoService {

    /**
     * Cria e persiste um novo tipo de pallet.
     *
     * @param nome         nome do tipo; obrigatório
     * @param capacidadeKg capacidade máxima em kg; deve ser positiva
     * @return {@link PalletTipo} criado
     * @throws IllegalArgumentException se validação falhar
     */
    PalletTipo create(String nome, BigDecimal capacidadeKg);

    /**
     * Obtém um tipo de pallet pelo seu identificador único.
     *
     * @param id UUID do tipo de pallet
     * @return {@link PalletTipo} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Tipo de pallet não encontrado"}
     */
    PalletTipo getById(UUID id);

    /**
     * Retorna todos os tipos de pallet activos.
     *
     * @return lista de tipos activos; pode ser vazia
     */
    List<PalletTipo> getAll();

    /**
     * Retorna todos os registos de tipos de pallet, incluindo os eliminados.
     *
     * @return lista completa
     */
    List<PalletTipo> getAllIncludingInactive();

    /**
     * Actualiza um tipo de pallet existente. Campos {@code null} são ignorados.
     *
     * @param id           UUID do tipo de pallet
     * @param nome         novo nome; {@code null} mantém o valor actual
     * @param capacidadeKg nova capacidade; {@code null} mantém o valor actual
     * @return {@link PalletTipo} actualizado
     * @throws IllegalArgumentException com mensagem {@code "Tipo de pallet não encontrado"}
     */
    PalletTipo update(UUID id, String nome, BigDecimal capacidadeKg);

    /**
     * Executa soft-delete em cascata: {@code EncomendaPallet} e {@code EncomendaOrdem} associadas.
     *
     * @param id UUID do tipo de pallet
     * @throws IllegalArgumentException com mensagem {@code "Tipo de pallet não encontrado"}
     */
    void delete(UUID id);
}
