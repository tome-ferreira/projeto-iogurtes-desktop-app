package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IFornecedorApiService;
import com.gestaoiogurtes.models.fornecedor.CreateFornecedorRequest;
import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import com.gestaoiogurtes.models.fornecedor.UpdateFornecedorRequest;
import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.models.PaginatedResponse;
import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class FornecedorService {

    private IFornecedorApiService api() {
        return RetrofitClient.getInstance().getService(IFornecedorApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<FornecedorResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void getByTipo(String tipoId, int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorResponse>>> cb) {
        ApiQuery.execute(api().findAllByTipo(tipoId, page, size), cb);
    }

    public void create(CreateFornecedorRequest req, Consumer<QueryState<FornecedorResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateFornecedorRequest req, Consumer<QueryState<FornecedorResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }

    public void getAllTipos(int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorTipoResponse>>> cb) {
        ApiQuery.execute(api().findAllTipos(page, size), cb);
    }
}
