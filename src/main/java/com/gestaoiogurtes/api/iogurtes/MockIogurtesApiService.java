package com.gestaoiogurtes.api.iogurtes;

import com.gestaoiogurtes.model.IogurteVM;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of {@link IIogurtesApiService}.
 * All data lives in a local list seeded with sample records.
 *
 * Replace with {@link RealIogurtesApiService} via {@link IogurtesApiServiceFactory}
 * when a real backend is available.
 */
public class MockIogurtesApiService implements IIogurtesApiService {

    private final List<IogurteVM> dados = new ArrayList<>(List.of(
            new IogurteVM("IOG-001", "Iogurte Natural",
                    "Iogurte natural sem açúcar, 250g", 21,
                    new BigDecimal("0.89"), new BigDecimal("3.56"), 120, 12, true),
            new IogurteVM("IOG-002", "Iogurte Morango",
                    "Iogurte com polpa de morango, 250g", 18,
                    new BigDecimal("1.09"), new BigDecimal("4.36"), 85, 12, true),
            new IogurteVM("IOG-003", "Iogurte Grego",
                    "Iogurte grego proteico, 500g", 28,
                    new BigDecimal("2.49"), new BigDecimal("4.98"), 40, 6, true)));

    @Override
    public List<IogurteVM> listarTodos() {
        return dados;
    }

    @Override
    public void adicionar(IogurteVM iogurte) {
        dados.add(iogurte);
    }

    @Override
    public void atualizar(IogurteVM original, IogurteVM editado) {
        editado.id = original.id;
        editado.criadoEm = original.criadoEm;
        int idx = dados.indexOf(original);
        if (idx >= 0) {
            dados.set(idx, editado);
        }
    }

    @Override
    public void remover(IogurteVM iogurte) {
        dados.remove(iogurte);
    }
}
