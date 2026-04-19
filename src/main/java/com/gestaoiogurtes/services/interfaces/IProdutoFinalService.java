//v1.0
package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.bll.model.ProdutoFinal;
import com.gestaoiogurtes.bll.model.ProdutoMateria;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface espelho do {@code ProdutoFinalService} do BLL Spring Boot.
 */
public interface IProdutoFinalService {

    /**
     * Cria e persiste um novo produto final com os ingredientes associados.
     *
     * @param codigoSku     SKU único; obrigatório
     * @param nome          nome do produto; obrigatório e único
     * @param descricao     descrição longa; pode ser {@code null}
     * @param validadeDias  prazo de validade em dias
     * @param precoVenda    preço de venda
     * @param precoPorKg    preço por kg
     * @param quantidadeLote unidades por lote de produção; obrigatório
     * @param materias      lista de ingredientes; cada {@link ProdutoMateria} deve ter {@code materia} com ID válido
     * @return {@link ProdutoFinal} criado
     * @throws IllegalArgumentException se SKU ou nome duplicado, ou validação falhar
     */
    ProdutoFinal createProduto(String codigoSku, String nome, String descricao,
                               Integer validadeDias, BigDecimal precoVenda,
                               BigDecimal precoPorKg, Integer quantidadeLote,
                               List<ProdutoMateria> materias);

    /**
     * Actualiza um produto final existente.
     * <p>
     * <b>Nota:</b> o SKU e a lista de ingredientes NÃO são actualizáveis por este método — conforme § 7.7.
     * </p>
     *
     * @param id            UUID do produto
     * @param nome          novo nome
     * @param descricao     nova descrição
     * @param validadeDias  nova validade
     * @param precoVenda    novo preço de venda
     * @param precoPorKg    novo preço por kg
     * @param quantidadeLote nova quantidade de lote
     * @param visivelCliente novo estado de visibilidade
     * @return {@link ProdutoFinal} actualizado
     * @throws IllegalArgumentException com mensagem {@code "Produto não encontrado!"}
     */
    ProdutoFinal updateProduto(UUID id, String nome, String descricao,
                               Integer validadeDias, BigDecimal precoVenda,
                               BigDecimal precoPorKg, Integer quantidadeLote,
                               Boolean visivelCliente);

    /**
     * Obtém um produto final pelo seu identificador único.
     *
     * @param id UUID do produto
     * @return {@link ProdutoFinal} correspondente
     * @throws IllegalArgumentException com mensagem {@code "Produto não encontrado!"}
     */
    ProdutoFinal getById(UUID id);

    /**
     * Retorna todos os produtos finais activos.
     *
     * @return lista de produtos activos; pode ser vazia
     */
    List<ProdutoFinal> getAll();

    /**
     * Retorna todos os registos de produtos finais, incluindo os eliminados.
     *
     * @return lista completa
     */
    List<ProdutoFinal> getAllIncludingInactive();

    /**
     * Executa soft-delete em cascata: {@code ProdutoMateria}, {@code MovimentoStockPF},
     * {@code EncomendaPallet} e {@code EncomendaOrdem} relacionados.
     *
     * @param id UUID do produto
     * @throws IllegalArgumentException com mensagem {@code "Produto não encontrado!"}
     */
    void delete(UUID id);
}
