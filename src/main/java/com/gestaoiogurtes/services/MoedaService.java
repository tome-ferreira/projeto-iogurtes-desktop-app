package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.models.moeda.CreateMoedaRequest;
import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.models.moeda.UpdateMoedaRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class MoedaService {

    private com.gestaoiogurtes.api.services.IMoedaApiService api() {
        return RetrofitClient.getInstance()
                .getService(com.gestaoiogurtes.api.services.IMoedaApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<MoedaResponse>>> onStateChange) {
        ApiQuery.execute(api().findAll(page, size), onStateChange);
    }

    public void getById(String id, Consumer<QueryState<MoedaResponse>> onStateChange) {
        ApiQuery.execute(api().findById(id), onStateChange);
    }

    public void create(CreateMoedaRequest request, Consumer<QueryState<MoedaResponse>> onStateChange) {
        ApiQuery.execute(api().create(request), onStateChange);
    }

    public void update(String id, UpdateMoedaRequest request, Consumer<QueryState<MoedaResponse>> onStateChange) {
        ApiQuery.execute(api().update(id, request), onStateChange);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> onStateChange) {
        ApiQuery.execute(api().softDelete(id), onStateChange);
    }
}
