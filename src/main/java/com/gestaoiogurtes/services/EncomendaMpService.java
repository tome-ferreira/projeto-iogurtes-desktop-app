package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IEncomendaMpApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.encomendaMp.CreateEncomendaMpRequest;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;

import java.util.function.Consumer;

public class EncomendaMpService {

    private IEncomendaMpApiService api() {
        return RetrofitClient.getInstance().getService(IEncomendaMpApiService.class);
    }

    public void getAll(int page, int size,
            Consumer<QueryState<PaginatedResponse<EncomendaMpResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getByEstado(String estado, int page, int size,
            Consumer<QueryState<PaginatedResponse<EncomendaMpResponse>>> cb) {
        ApiQuery.execute(api().findByEstado(estado, page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateEncomendaMpRequest request,
            Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().create(request), cb);
    }

    public void aprovar(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().aprovar(id), cb);
    }

    public void cancelar(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().cancelar(id), cb);
    }

    public void marcarRecebida(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().marcarRecebida(id), cb);
    }
}
