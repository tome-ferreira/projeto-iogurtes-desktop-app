//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.PalletTipo;
import com.gestaoiogurtes.services.interfaces.IPalletTipoService;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementação mock em memória de {@link IPalletTipoService}.
 * Dados pré-populados a partir de {@link MockDataFactory}.
 */
public class MockPalletTipoService implements IPalletTipoService {

    private final Map<UUID, PalletTipo> store;

    public MockPalletTipoService() {
        this.store = MockDataFactory.criarPalletTipos();
    }

    @Override
    public PalletTipo create(String nome, BigDecimal capacidadeKg) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome é obrigatório");
        if (capacidadeKg == null || capacidadeKg.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Capacidade deve ser positiva");

        PalletTipo pt = new PalletTipo(nome, capacidadeKg);
        store.put(pt.getId(), pt);
        return pt;
    }

    @Override
    public PalletTipo getById(UUID id) {
        PalletTipo pt = store.get(id);
        if (pt == null)
            throw new IllegalArgumentException("Tipo de pallet não encontrado");
        return pt;
    }

    @Override
    public List<PalletTipo> getAll() {
        return store.values().stream()
                .filter(PalletTipo::isActive)
                .toList();
    }

    @Override
    public List<PalletTipo> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public PalletTipo update(UUID id, String nome, BigDecimal capacidadeKg) {
        PalletTipo pt = getById(id);
        if (nome != null)          pt.setNome(nome);
        if (capacidadeKg != null)  pt.setCapacidadeKg(capacidadeKg);
        return pt;
    }

    @Override
    public void delete(UUID id) {
        PalletTipo pt = getById(id);
        pt.softDelete();
    }
}
