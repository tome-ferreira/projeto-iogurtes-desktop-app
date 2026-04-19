//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.Empresa;

import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code EmpresaService} do BLL Spring Boot.
 * <p>
 * Permite trocar entre implementação mock (em memória) e real (JAR Spring Boot)
 * sem alterar o código dos controllers JavaFX.
 * </p>
 */
public interface IEmpresaService {

    /**
     * Cria e persiste uma nova empresa.
     *
     * @param nomeEmpresa nome comercial da empresa; obrigatório
     * @param nipc        NIPC único; obrigatório; validado contra duplicados
     * @param telefone    telefone normalizado; pode ser {@code null}
     * @param morada      morada completa; obrigatória
     * @param codigoPostal código postal; obrigatório
     * @param cidade      cidade; obrigatória
     * @return {@link Empresa} criada com {@code id} preenchido
     * @throws IllegalArgumentException se validação falhar (NIPC duplicado, campos obrigatórios em falta, etc.)
     */
    Empresa createEmpresa(String nomeEmpresa, String nipc, String telefone,
                          String morada, String codigoPostal, String cidade);

    /**
     * Obtém uma empresa pelo seu identificador único.
     * Inclui registos inactivos no lookup.
     *
     * @param id UUID da empresa
     * @return {@link Empresa} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Empresa não encontrada!"} se não existir
     */
    Empresa getById(UUID id);

    /**
     * Retorna todas as empresas activas ({@code isActive = true}).
     *
     * @return lista de empresas activas; pode ser vazia
     */
    List<Empresa> getAll();

    /**
     * Retorna todos os registos de empresas, incluindo os eliminados (soft-deleted).
     *
     * @return lista completa de empresas
     */
    List<Empresa> getAllIncludingInactive();

    /**
     * Actualiza os dados de uma empresa existente.
     *
     * @param id          UUID da empresa a actualizar
     * @param nomeEmpresa novo nome comercial; obrigatório
     * @param nipc        novo NIPC; obrigatório
     * @param telefone    novo telefone normalizado; pode ser {@code null}
     * @param morada      nova morada; obrigatória
     * @param codigoPostal novo código postal; obrigatório
     * @param cidade      nova cidade; obrigatória
     * @return {@link Empresa} actualizada
     * @throws IllegalArgumentException se não existir ou validação falhar
     */
    Empresa update(UUID id, String nomeEmpresa, String nipc, String telefone,
                   String morada, String codigoPostal, String cidade);

    /**
     * Executa soft-delete da empresa e de todos os utilizadores associados.
     *
     * @param id UUID da empresa a eliminar
     * @throws IllegalArgumentException com mensagem {@code "Empresa não encontrada!"} se não existir
     */
    void delete(UUID id);
}
