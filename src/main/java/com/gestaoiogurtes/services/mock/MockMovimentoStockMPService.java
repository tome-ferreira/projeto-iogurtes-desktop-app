//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.MateriaPrima;
import com.gestaoiogurtes.bll.model.MovimentoStockMP;
import com.gestaoiogurtes.bll.model.User;
import com.gestaoiogurtes.bll.model.enums.TipoMovimentoMP;
import com.gestaoiogurtes.services.interfaces.IMovimentoStockMPService;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação mock em memória de {@link IMovimentoStockMPService}.
 * <p>
 * Thread-safe: operações de escrita são sincronizadas (o contrato do BLL
 * marca este serviço como {@code @Transactional}).
 * </p>
 */
public class MockMovimentoStockMPService implements IMovimentoStockMPService {

    private final Map<UUID, MovimentoStockMP> store = Collections.synchronizedMap(new LinkedHashMap<>());
    private final MockMateriaPrimaService materiaPrimaService;
    private final MockUserService userService;

    public MockMovimentoStockMPService(MockMateriaPrimaService materiaPrimaService,
                                       MockUserService userService) {
        this.materiaPrimaService = materiaPrimaService;
        this.userService = userService;
    }

    @Override
    public synchronized MovimentoStockMP registarMovimento(UUID userId, UUID materiaId,
                                                            TipoMovimentoMP tipo,
                                                            BigDecimal quantidade,
                                                            String observacao) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Quantidade deve ser positiva");

        User user          = userService.getById(userId);
        MateriaPrima mp    = materiaPrimaService.getById(materiaId);

        switch (tipo) {
            case ENTRADA -> mp.setStockAtual(mp.getStockAtual().add(quantidade));
            case SAIDA   -> mp.setStockAtual(mp.getStockAtual().subtract(quantidade));
            case AJUSTE  -> mp.setStockAtual(quantidade);
        }

        MovimentoStockMP mov = new MovimentoStockMP(user, mp, tipo, quantidade, observacao);
        store.put(mov.getId(), mov);
        return mov;
    }

    @Override
    public List<MovimentoStockMP> getByMateria(UUID materiaId) {
        return store.values().stream()
                .filter(m -> m.getMateria().getId().equals(materiaId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentoStockMP> getByUser(UUID userId) {
        return store.values().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentoStockMP> getAll() {
        return store.values().stream()
                .filter(MovimentoStockMP::isActive)
                .toList();
    }

    @Override
    public List<MovimentoStockMP> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }
}
