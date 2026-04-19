//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.MovimentoStockPF;
import com.gestaoiogurtes.bll.model.OrdemProducao;
import com.gestaoiogurtes.bll.model.ProdutoFinal;
import com.gestaoiogurtes.bll.model.enums.TipoMovimentoPF;
import com.gestaoiogurtes.services.interfaces.IMovimentoStockPFService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação mock em memória de {@link IMovimentoStockPFService}.
 * <p>
 * <b>Atenção (§ 7.6):</b> os parâmetros são objectos completos, não IDs.
 * </p>
 * <p>
 * Thread-safe: operações de escrita são sincronizadas.
 * </p>
 */
public class MockMovimentoStockPFService implements IMovimentoStockPFService {

    private final Map<UUID, MovimentoStockPF> store = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public synchronized MovimentoStockPF registarMovimento(ProdutoFinal produto, OrdemProducao ordem,
                                                            TipoMovimentoPF tipo, Integer quantidade,
                                                            String observacao) {
        if (produto == null)
            throw new IllegalArgumentException("Produto não pode ser null");
        if (quantidade == null || quantidade < 0)
            throw new IllegalArgumentException("Quantidade deve ser não-negativa");

        int stockActual = produto.getStockAtual() != null ? produto.getStockAtual() : 0;

        switch (tipo) {
            case PRODUCAO, DEVOLUCAO -> produto.setStockAtual(stockActual + quantidade);
            case EXPEDICAO -> {
                if (stockActual < quantidade)
                    throw new IllegalStateException(
                            "Stock insuficiente para o produto: " + produto.getNome()
                            + " (stock=" + stockActual + ", pedido=" + quantidade + ")");
                produto.setStockAtual(stockActual - quantidade);
            }
            case AJUSTE -> produto.setStockAtual(quantidade);
        }

        MovimentoStockPF mov = new MovimentoStockPF(produto, ordem, tipo, quantidade, observacao);
        store.put(mov.getId(), mov);
        return mov;
    }

    @Override
    public MovimentoStockPF getById(UUID id) {
        MovimentoStockPF m = store.get(id);
        if (m == null)
            throw new IllegalArgumentException("Movimento não encontrado");
        return m;
    }

    @Override
    public List<MovimentoStockPF> getAll() {
        return store.values().stream()
                .filter(MovimentoStockPF::isActive)
                .toList();
    }

    @Override
    public List<MovimentoStockPF> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<MovimentoStockPF> getByProduto(UUID produtoId) {
        return store.values().stream()
                .filter(m -> m.getProduto().getId().equals(produtoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimentoStockPF> getByOrdem(UUID ordemId) {
        return store.values().stream()
                .filter(m -> m.getOrdem() != null && m.getOrdem().getId().equals(ordemId))
                .collect(Collectors.toList());
    }
}
