//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.Fornecedor;
import com.gestaoiogurtes.bll.model.FornecedorCertificacao;
import com.gestaoiogurtes.services.interfaces.IFornecedorService;

import java.util.*;

/**
 * Implementação mock em memória de {@link IFornecedorService}.
 * Dados pré-populados a partir de {@link MockDataFactory}.
 */
public class MockFornecedorService implements IFornecedorService {

    private final Map<UUID, Fornecedor> store;

    public MockFornecedorService() {
        this.store = MockDataFactory.criarFornecedores();
    }

    @Override
    public Fornecedor createFornecedor(String nome, String nif, String email,
                                       String telefone, String morada,
                                       List<FornecedorCertificacao> certificacoes) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do fornecedor é obrigatório");
        if (morada == null || morada.isBlank())
            throw new IllegalArgumentException("Morada é obrigatória");

        Fornecedor f = new Fornecedor(nome, nif, email, telefone, morada);
        List<FornecedorCertificacao> certs = new ArrayList<>(certificacoes);
        certs.forEach(c -> {
            c.setId(UUID.randomUUID());
            c.setFornecedor(f);
        });
        f.setCertificacoes(certs);
        store.put(f.getId(), f);
        return f;
    }

    @Override
    public Fornecedor updateFornecedor(UUID id, String nome, String nif, String email,
                                       String telefone, String morada) {
        Fornecedor f = getById(id);
        if (nome != null) f.setNome(nome);
        if (nif != null)  f.setNif(nif);
        if (email != null) f.setEmail(email);
        if (telefone != null) f.setTelefone(telefone);
        if (morada != null) f.setMorada(morada);
        return f;
    }

    @Override
    public Fornecedor getById(UUID id) {
        Fornecedor f = store.get(id);
        if (f == null)
            throw new IllegalArgumentException("Fornecedor não encontrado");
        return f;
    }

    @Override
    public List<Fornecedor> getAll() {
        return store.values().stream()
                .filter(Fornecedor::isActive)
                .toList();
    }

    @Override
    public List<Fornecedor> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        Fornecedor f = getById(id);
        f.getCertificacoes().forEach(FornecedorCertificacao::softDelete);
        f.softDelete();
    }
}
