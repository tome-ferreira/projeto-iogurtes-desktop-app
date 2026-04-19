//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.Empresa;
import com.gestaoiogurtes.services.interfaces.IEmpresaService;

import java.util.*;

/**
 * Implementação mock em memória de {@link IEmpresaService}.
 * Dados pré-populados a partir de {@link MockDataFactory}.
 */
public class MockEmpresaService implements IEmpresaService {

    private final Map<UUID, Empresa> store;

    public MockEmpresaService() {
        this.store = MockDataFactory.criarEmpresas();
    }

    @Override
    public Empresa createEmpresa(String nomeEmpresa, String nipc, String telefone,
                                  String morada, String codigoPostal, String cidade) {
        if (nomeEmpresa == null || nomeEmpresa.isBlank())
            throw new IllegalArgumentException("Nome da empresa é obrigatório");
        if (nipc == null || nipc.isBlank())
            throw new IllegalArgumentException("NIPC é obrigatório");
        boolean nipcDuplicado = store.values().stream()
                .anyMatch(e -> e.getNipc().equals(nipc) && e.isActive());
        if (nipcDuplicado)
            throw new IllegalArgumentException("NIPC já existe: " + nipc);
        if (morada == null || morada.isBlank())
            throw new IllegalArgumentException("Morada é obrigatória");
        if (codigoPostal == null || codigoPostal.isBlank())
            throw new IllegalArgumentException("Código postal é obrigatório");
        if (cidade == null || cidade.isBlank())
            throw new IllegalArgumentException("Cidade é obrigatória");

        Empresa empresa = new Empresa(nomeEmpresa, nipc, telefone, morada, codigoPostal, cidade);
        store.put(empresa.getId(), empresa);
        return empresa;
    }

    @Override
    public Empresa getById(UUID id) {
        Empresa e = store.get(id);
        if (e == null)
            throw new IllegalArgumentException("Empresa não encontrada!");
        return e;
    }

    @Override
    public List<Empresa> getAll() {
        return store.values().stream()
                .filter(Empresa::isActive)
                .toList();
    }

    @Override
    public List<Empresa> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Empresa update(UUID id, String nomeEmpresa, String nipc, String telefone,
                           String morada, String codigoPostal, String cidade) {
        Empresa empresa = getById(id);
        if (nomeEmpresa == null || nomeEmpresa.isBlank())
            throw new IllegalArgumentException("Nome da empresa é obrigatório");
        empresa.setNomeEmpresa(nomeEmpresa);
        empresa.setNipc(nipc);
        empresa.setTelefone(telefone);
        empresa.setMorada(morada);
        empresa.setCodigoPostal(codigoPostal);
        empresa.setCidade(cidade);
        return empresa;
    }

    @Override
    public void delete(UUID id) {
        Empresa empresa = getById(id);
        empresa.softDelete();
    }
}
