//v1.0
package com.gestaoiogurtes.services.mock;

import com.gestaoiogurtes.bll.model.*;
import com.gestaoiogurtes.bll.model.enums.EstadoEncomenda;
import com.gestaoiogurtes.bll.model.enums.EstadoOrdem;
import com.gestaoiogurtes.services.interfaces.IEncomendaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementação mock em memória de {@link IEncomendaService}.
 * <p>
 * Simula a lógica de stock automático:
 * se houver stock suficiente, expede imediatamente;
 * caso contrário cria uma {@link OrdemProducao} em {@code AGUARDA_APROVACAO}.
 * </p>
 */
public class MockEncomendaService implements IEncomendaService {

    private final Map<UUID, Encomenda> store = new LinkedHashMap<>();
    private final MockUserService userService;
    private final MockProdutoFinalService produtoService;
    private final MockPalletTipoService palletTipoService;
    private final MockOrdemProducaoService ordemService;

    public MockEncomendaService(MockUserService userService,
                                MockProdutoFinalService produtoService,
                                MockPalletTipoService palletTipoService,
                                MockOrdemProducaoService ordemService) {
        this.userService       = userService;
        this.produtoService    = produtoService;
        this.palletTipoService = palletTipoService;
        this.ordemService      = ordemService;
    }

    @Override
    public Encomenda createEncomenda(UUID userId, List<EncomendaPallet> pallets) {
        User user = userService.getById(userId);

        BigDecimal totalPreco = pallets.stream()
                .map(p -> p.getPrecoPorPallet()
                        .multiply(BigDecimal.valueOf(p.getQuantidadePallets())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Encomenda encomenda = new Encomenda(user, totalPreco);

        boolean todosComStock  = true;
        List<EncomendaPallet> palletsMutaveis = new ArrayList<>(pallets);

        for (EncomendaPallet ep : palletsMutaveis) {
            ep.setId(UUID.randomUUID());
            ep.setEncomenda(encomenda);

            ProdutoFinal produto    = produtoService.getById(ep.getProduto().getId());
            PalletTipo   palletTipo = palletTipoService.getById(ep.getPalletTipo().getId());

            int kgNecessarios = palletTipo.getCapacidadeKg()
                    .multiply(BigDecimal.valueOf(ep.getQuantidadePallets())).intValue();

            if (produto.getStockAtual() >= kgNecessarios) {
                produto.setStockAtual(produto.getStockAtual() - kgNecessarios);
                ep.setOrdens(new ArrayList<>());
            } else {
                todosComStock = false;
                // Cria ordem pendente sem consumo imediato de MP
                OrdemProducao ordemPendente = new OrdemProducao(
                        user,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(8),
                        "Ordem gerada automaticamente para encomenda " + encomenda.getId()
                );
                ordemPendente.setEstado(EstadoOrdem.AGUARDA_APROVACAO);
                ordemPendente.setProdutos(new ArrayList<>());
                ordemPendente.setConsumos(new ArrayList<>());

                EncomendaOrdem eo = new EncomendaOrdem(ordemPendente, ep, ep.getQuantidadePallets());
                ep.setOrdens(new ArrayList<>(List.of(eo)));
            }
        }

        encomenda.setPallets(palletsMutaveis);
        encomenda.setEstado(todosComStock ? EstadoEncomenda.confirmada : EstadoEncomenda.pendente);
        store.put(encomenda.getId(), encomenda);
        return encomenda;
    }

    @Override
    public Encomenda getById(UUID id) {
        Encomenda e = store.get(id);
        if (e == null)
            throw new IllegalArgumentException("Encomenda não encontrada");
        return e;
    }

    @Override
    public List<Encomenda> getAll() {
        return store.values().stream()
                .filter(Encomenda::isActive)
                .toList();
    }

    @Override
    public List<Encomenda> getAllIncludingInactive() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(UUID id) {
        Encomenda e = getById(id);
        if (e.getPallets() != null) {
            e.getPallets().forEach(pallet -> {
                if (pallet.getOrdens() != null)
                    pallet.getOrdens().forEach(EncomendaOrdem::softDelete);
                pallet.softDelete();
            });
        }
        e.softDelete();
    }
}
