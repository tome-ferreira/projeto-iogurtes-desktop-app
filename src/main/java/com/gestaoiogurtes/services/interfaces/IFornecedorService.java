//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.Fornecedor;
import com.gestaoiogurtes.bll.model.FornecedorCertificacao;

import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code FornecedorService} do BLL Spring Boot.
 */
public interface IFornecedorService {

    /**
     * Cria e persiste um novo fornecedor com as certificações associadas.
     *
     * @param nome          nome do fornecedor; obrigatório
     * @param nif           NIF único; pode ser {@code null}
     * @param email         email de contacto com formato válido; pode ser {@code null}
     * @param telefone      telefone normalizado; pode ser {@code null}
     * @param morada        morada; obrigatória
     * @param certificacoes lista de {@link FornecedorCertificacao} já construídas
     *                      (o campo {@code fornecedor} é preenchido internamente)
     * @return {@link Fornecedor} persistido
     * @throws IllegalArgumentException se validação falhar
     */
    Fornecedor createFornecedor(String nome, String nif, String email,
                                String telefone, String morada,
                                List<FornecedorCertificacao> certificacoes);

    /**
     * Actualiza os campos escalares de um fornecedor (sem mudar certificações).
     *
     * @param id      UUID do fornecedor
     * @param nome    novo nome
     * @param nif     novo NIF
     * @param email   novo email
     * @param telefone novo telefone
     * @param morada  nova morada
     * @return {@link Fornecedor} actualizado
     * @throws IllegalArgumentException com mensagem {@code "Fornecedor não encontrado"}
     */
    Fornecedor updateFornecedor(UUID id, String nome, String nif, String email,
                                String telefone, String morada);

    /**
     * Obtém um fornecedor pelo seu identificador único.
     *
     * @param id UUID do fornecedor
     * @return {@link Fornecedor} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Fornecedor não encontrado"}
     */
    Fornecedor getById(UUID id);

    /**
     * Retorna todos os fornecedores activos.
     *
     * @return lista de fornecedores activos; pode ser vazia
     */
    List<Fornecedor> getAll();

    /**
     * Retorna todos os registos de fornecedores, incluindo os eliminados.
     *
     * @return lista completa de fornecedores
     */
    List<Fornecedor> getAllIncludingInactive();

    /**
     * Executa soft-delete em cascata: matérias-primas e certificações do fornecedor.
     *
     * @param id UUID do fornecedor
     * @throws IllegalArgumentException com mensagem {@code "Fornecedor não encontrado"}
     */
    void delete(UUID id);
}
