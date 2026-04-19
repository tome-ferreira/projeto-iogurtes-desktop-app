//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.Fornecedor;
import com.gestaoiogurtes.bll.model.MateriaPrima;
import com.gestaoiogurtes.bll.model.enums.TipoMateriaPrima;
import com.gestaoiogurtes.services.interfaces.IMateriaPrimaService;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementação mock em memória de {@link IMateriaPrimaService}.
 * Dados pré-populados a partir de {@link MockDataFactory}.
 */
public class MockMateriaPrimaService implements IMateriaPrimaService {

    private final Map<UUID, MateriaPrima> store;
    private final MockFornecedorService fornecedorService;

    public MockMateriaPrimaService(MockFornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
        this.store = MockDataFactory.criarMateriasPrimas(
                MockDataFactory.criarFornecedores()
        );
    }

    @Override
    public MateriaPrima createMateriaPrima(String nome, String unidade, TipoMateriaPrima tipo,
                                           BigDecimal stockAtual, BigDecimal stockMinimo,
                                           BigDecimal precoUnitario, UUID fornecedorId) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço deve ser não-negativo");

        Fornecedor f = fornecedorService.getById(fornecedorId);
        MateriaPrima mp = new MateriaPrima(nome, tipo, unidade, stockAtual, stockMinimo, precoUnitario, f);
        store.put(mp.getId(), mp);
        return mp;
    }

    @Override
    public MateriaPrima updateMateriaPrima(UUID id, String nome, String unidade, TipoMateriaPrima tipo,
                                           BigDecimal stockMinimo, BigDecimal precoUnitario,
                                           UUID fornecedorId) {
        MateriaPrima mp = getById(id);
        Fornecedor f = fornecedorService.getById(fornecedorId);
        if (nome != null)          mp.setNome(nome);
        if (unidade != null)       mp.setUnidade(unidade);
        if (tipo != null)          mp.setTipo(tipo);
        if (stockMinimo != null)   mp.setStockMinimo(stockMinimo);
        if (precoUnitario != null) mp.setPrecoUnitario(precoUnitario);
        mp.setFornecedor(f);
        return mp;
    }

    @Override
    public MateriaPrima getById(UUID id) {
        MateriaPrima mp = store.get(id);
        if (mp == null)
            throw new IllegalArgumentException("Matéria prima não encontrada");
        return mp;
    }

    @Override
    public List<MateriaPrima> getAll() {
        return store.values().stream()
                .filter(MateriaPrima::isActive)
                .toList();
    }

    @Override
    public List<MateriaPrima> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        MateriaPrima mp = getById(id);
        mp.softDelete();
    }
}
