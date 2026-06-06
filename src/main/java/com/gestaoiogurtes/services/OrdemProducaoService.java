package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IOrdemProducaoApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.models.ordemProducao.CreateOrdemProducaoRequest;
import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoResponse;

import java.util.function.Consumer;

public class OrdemProducaoService {

    private IOrdemProducaoApiService api() {
        return RetrofitClient.getInstance().getService(IOrdemProducaoApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<OrdemProducaoResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getByEstado(String estado, int page, int size, Consumer<QueryState<PaginatedResponse<OrdemProducaoResponse>>> cb) {
        ApiQuery.execute(api().findByEstado(estado, page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<OrdemProducaoResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateOrdemProducaoRequest request, Consumer<QueryState<OrdemProducaoResponse>> cb) {
        ApiQuery.execute(api().create(request), cb);
    }

    public void getAllProdutos(int page, int size, Consumer<QueryState<PaginatedResponse<ProdutoFinalResponse>>> cb) {
        ApiQuery.execute(api().findAllProdutosFinais(page, size), cb);
    }

    public void aprovar(String id, Consumer<QueryState<OrdemProducaoResponse>> cb) {
        ApiQuery.execute(api().aprovar(id), cb);
    }

    public void cancelar(String id, Consumer<QueryState<OrdemProducaoResponse>> cb) {
        ApiQuery.execute(api().cancelar(id), cb);
    }

    public void concluir(String id, Consumer<QueryState<OrdemProducaoResponse>> cb) {
        ApiQuery.execute(api().concluir(id), cb);
    }
}
