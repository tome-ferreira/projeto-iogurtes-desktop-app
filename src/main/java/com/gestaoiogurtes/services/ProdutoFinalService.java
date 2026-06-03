package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IProdutoFinalApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.produtoFinal.CreateProdutoFinalRequest;
import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.models.produtoFinal.UpdateProdutoFinalRequest;
import okhttp3.ResponseBody;

import java.util.function.Consumer;

/**
 * Serviço de aplicação para Produtos Finais.
 * Delega todas as chamadas HTTP ao {@link IProdutoFinalApiService} via
 * {@link ApiQuery}, que garante {@code Platform.runLater()} internamente.
 */
public class ProdutoFinalService {

    private IProdutoFinalApiService api() {
        return RetrofitClient.getInstance()
                .getService(IProdutoFinalApiService.class);
    }

    public void getAll(int page, int size,
                       Consumer<QueryState<PaginatedResponse<ProdutoFinalResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<ProdutoFinalResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateProdutoFinalRequest req,
                       Consumer<QueryState<ProdutoFinalResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateProdutoFinalRequest req,
                       Consumer<QueryState<ProdutoFinalResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }

    public void addMateriasComposicao(String produtoId, com.gestaoiogurtes.models.produtoFinal.AddMateriasComposicaoRequest req,
                                      Consumer<QueryState<ProdutoFinalResponse>> cb) {
        ApiQuery.execute(api().addMateriasComposicao(produtoId, req), cb);
    }

    public void removeComposicao(String produtoId, String composicaoId,
                                 Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().removeComposicao(produtoId, composicaoId), cb);
    }

}
