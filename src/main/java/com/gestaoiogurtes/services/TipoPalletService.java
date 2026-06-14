package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.ITipoPalletApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.tipoPallet.*;
import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class TipoPalletService {

    private ITipoPalletApiService api() {
        return RetrofitClient.getInstance().getService(ITipoPalletApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<TipoPalletResponse>>> cb) {
        ApiQuery.execute(api().findAllActive(page, size, "nome", "asc"), cb);
    }

    public void getById(String id, Consumer<QueryState<TipoPalletResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateTipoPalletRequest req, Consumer<QueryState<TipoPalletResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateTipoPalletRequest req, Consumer<QueryState<TipoPalletResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }
}
