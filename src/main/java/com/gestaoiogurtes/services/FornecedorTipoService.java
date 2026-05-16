package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IFornecedorTipoApiService;
import com.gestaoiogurtes.models.fornecedortipo.CreateFornecedorTipoRequest;
import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.models.fornecedortipo.UpdateFornecedorTipoRequest;
import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

public class FornecedorTipoService {

    private IFornecedorTipoApiService api() {
        return RetrofitClient.getInstance().getService(IFornecedorTipoApiService.class);
    }

    public void getAll(Consumer<QueryState<List<FornecedorTipoResponse>>> cb) {
        ApiQuery.execute(api().findAllActive(), cb);
    }

    public void getById(String id, Consumer<QueryState<FornecedorTipoResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateFornecedorTipoRequest req, Consumer<QueryState<FornecedorTipoResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateFornecedorTipoRequest req, Consumer<QueryState<FornecedorTipoResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }
}
