//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.ProdutoFinal;
import com.gestaoiogurtes.bll.model.ProdutoMateria;
import com.gestaoiogurtes.services.interfaces.IProdutoFinalService;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementação mock em memória de {@link IProdutoFinalService}.
 * Dados pré-populados a partir de {@link MockDataFactory}.
 */
public class MockProdutoFinalService implements IProdutoFinalService {

    private final Map<UUID, ProdutoFinal> store;

    public MockProdutoFinalService(MockMateriaPrimaService mpService) {
        // Usa os dados das matérias-primas já criados para construir os produtos
        this.store = MockDataFactory.criarProdutos(
                MockDataFactory.criarMateriasPrimas(MockDataFactory.criarFornecedores())
        );
    }

    @Override
    public ProdutoFinal createProduto(String codigoSku, String nome, String descricao,
                                      Integer validadeDias, BigDecimal precoVenda,
                                      BigDecimal precoPorKg, Integer quantidadeLote,
                                      List<ProdutoMateria> materias) {
        if (codigoSku == null || codigoSku.isBlank())
            throw new IllegalArgumentException("SKU é obrigatório");
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");

        boolean skuDup = store.values().stream()
                .anyMatch(p -> p.getCodigoSku().equals(codigoSku) && p.isActive());
        if (skuDup)
            throw new IllegalArgumentException("SKU já existe: " + codigoSku);

        boolean nomeDup = store.values().stream()
                .anyMatch(p -> p.getNome().equals(nome) && p.isActive());
        if (nomeDup)
            throw new IllegalArgumentException("Nome de produto já existe: " + nome);

        ProdutoFinal produto = new ProdutoFinal(codigoSku, nome, descricao,
                validadeDias, precoVenda, precoPorKg, quantidadeLote);
        produto.setStockAtual(0);
        produto.setVisivelCliente(false);

        List<ProdutoMateria> mats = new ArrayList<>(materias);
        mats.forEach(m -> {
            m.setId(UUID.randomUUID());
            m.setProduto(produto);
        });
        produto.setMaterias(mats);
        store.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public ProdutoFinal updateProduto(UUID id, String nome, String descricao,
                                      Integer validadeDias, BigDecimal precoVenda,
                                      BigDecimal precoPorKg, Integer quantidadeLote,
                                      Boolean visivelCliente) {
        ProdutoFinal p = getById(id);
        if (nome != null)            p.setNome(nome);
        if (descricao != null)       p.setDescricao(descricao);
        if (validadeDias != null)    p.setValidadeDias(validadeDias);
        if (precoVenda != null)      p.setPrecoVenda(precoVenda);
        if (precoPorKg != null)      p.setPrecoPorKg(precoPorKg);
        if (quantidadeLote != null)  p.setQuantidadeLote(quantidadeLote);
        if (visivelCliente != null)  p.setVisivelCliente(visivelCliente);
        return p;
    }

    @Override
    public ProdutoFinal getById(UUID id) {
        ProdutoFinal p = store.get(id);
        if (p == null)
            throw new IllegalArgumentException("Produto não encontrado!");
        return p;
    }

    @Override
    public List<ProdutoFinal> getAll() {
        return store.values().stream()
                .filter(ProdutoFinal::isActive)
                .toList();
    }

    @Override
    public List<ProdutoFinal> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        ProdutoFinal p = getById(id);
        p.getMaterias().forEach(ProdutoMateria::softDelete);
        p.softDelete();
    }
}
