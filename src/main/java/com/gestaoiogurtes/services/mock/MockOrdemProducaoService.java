//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.*;
import com.gestaoiogurtes.bll.model.enums.EstadoOrdem;
import com.gestaoiogurtes.bll.model.enums.TipoMovimentoMP;
import com.gestaoiogurtes.services.interfaces.IOrdemProducaoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementação mock em memória de {@link IOrdemProducaoService}.
 * <p>
 * Simula o cálculo de consumos de matérias-primas e os movimentos de stock.
 * </p>
 */
public class MockOrdemProducaoService implements IOrdemProducaoService {

    private final Map<UUID, OrdemProducao> store = new LinkedHashMap<>();
    private final MockUserService userService;
    private final MockProdutoFinalService produtoService;
    private final MockMovimentoStockMPService stockMPService;

    public MockOrdemProducaoService(MockUserService userService,
                                    MockProdutoFinalService produtoService,
                                    MockMovimentoStockMPService stockMPService) {
        this.userService   = userService;
        this.produtoService = produtoService;
        this.stockMPService = stockMPService;
    }

    @Override
    public OrdemProducao createOrdem(UUID userId, LocalDateTime dataInicio,
                                     LocalDateTime dataFim, String observacoes,
                                     List<OrdemProducaoProduto> produtos) {
        User user = userService.getById(userId);

        OrdemProducao ordem = new OrdemProducao(user, dataInicio, dataFim, observacoes);
        ordem.setEstado(EstadoOrdem.EM_PRODUCAO);

        List<ConsumoProducao>       todosConsumos   = new ArrayList<>();
        List<OrdemProducaoProduto>  produtosMutaveis = new ArrayList<>(produtos);

        for (OrdemProducaoProduto opp : produtosMutaveis) {
            opp.setId(UUID.randomUUID());
            opp.setOrdem(ordem);

            ProdutoFinal produto = produtoService.getById(opp.getProduto().getId());

            for (ProdutoMateria pm : produto.getMaterias()) {
                BigDecimal consumoTotal = pm.getQuantidadePorUnidadeProduto()
                        .multiply(opp.getQuantidadeKg());
                UUID materiaId = pm.getMateria().getId();

                // Agregar consumos da mesma matéria-prima
                todosConsumos.stream()
                        .filter(c -> c.getMateria().getId().equals(materiaId))
                        .findFirst()
                        .ifPresentOrElse(
                                ex -> ex.setQuantidadeKg(ex.getQuantidadeKg().add(consumoTotal)),
                                () -> todosConsumos.add(
                                        new ConsumoProducao(ordem, pm.getMateria(), consumoTotal))
                        );

                stockMPService.registarMovimento(
                        userId, materiaId, TipoMovimentoMP.SAIDA, consumoTotal,
                        "Consumo para ordem " + ordem.getId()
                );
            }
        }

        ordem.setProdutos(produtosMutaveis);
        ordem.setConsumos(todosConsumos);
        store.put(ordem.getId(), ordem);
        return ordem;
    }

    @Override
    public OrdemProducao getById(UUID id) {
        OrdemProducao o = store.get(id);
        if (o == null)
            throw new IllegalArgumentException("Ordem não encontrada");
        return o;
    }

    @Override
    public List<OrdemProducao> getAll() {
        return store.values().stream()
                .filter(OrdemProducao::isActive)
                .toList();
    }

    @Override
    public List<OrdemProducao> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public OrdemProducao updateOrdem(UUID id, LocalDateTime dataInicio, LocalDateTime dataFim,
                                     String observacoes) {
        OrdemProducao ordem = getById(id);
        if (dataInicio != null)  ordem.setDataInicio(dataInicio);
        if (dataFim != null)     ordem.setDataFim(dataFim);
        if (observacoes != null) ordem.setObservacoes(observacoes);
        return ordem;
    }

    @Override
    public OrdemProducao cancelarOrdem(UUID id, UUID userId) {
        OrdemProducao ordem = getById(id);
        // Reverter consumos de MP
        for (ConsumoProducao consumo : ordem.getConsumos()) {
            stockMPService.registarMovimento(
                    userId, consumo.getMateria().getId(),
                    TipoMovimentoMP.ENTRADA, consumo.getQuantidadeKg(),
                    "Reversão por cancelamento da ordem " + ordem.getId()
            );
        }
        ordem.setEstado(EstadoOrdem.CANCELADA);
        return ordem;
    }

    @Override
    public OrdemProducao aprovarOrdem(UUID ordemId) {
        OrdemProducao ordem = getById(ordemId);
        if (ordem.getEstado() != EstadoOrdem.AGUARDA_APROVACAO)
            throw new IllegalStateException("Ordem não está em estado de aprovação");
        ordem.setEstado(EstadoOrdem.EM_PRODUCAO);
        ordem.setAprovadoEm(LocalDateTime.now());
        return ordem;
    }

    @Override
    public void delete(UUID id) {
        OrdemProducao ordem = getById(id);
        ordem.getProdutos().forEach(OrdemProducaoProduto::softDelete);
        ordem.getConsumos().forEach(ConsumoProducao::softDelete);
        ordem.softDelete();
    }
}
