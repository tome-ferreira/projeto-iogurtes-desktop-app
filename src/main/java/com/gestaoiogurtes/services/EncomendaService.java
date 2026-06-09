package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IEncomendaApiService;
import com.gestaoiogurtes.models.encomenda.EncomendaDetalheResponse;
import com.gestaoiogurtes.models.encomenda.PageEncomendaResponse;
import com.gestaoiogurtes.models.encomenda.EncomendaResponse;

import java.util.function.Consumer;

public class EncomendaService {

    private IEncomendaApiService api() {
        return RetrofitClient.getInstance()
                .getService(IEncomendaApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PageEncomendaResponse>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getByEstado(String estado, int page, int size, Consumer<QueryState<PageEncomendaResponse>> cb) {
        ApiQuery.execute(api().findByEstado(estado, page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<EncomendaDetalheResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void confirmar(String id, Consumer<QueryState<EncomendaResponse>> cb) {
        ApiQuery.execute(api().confirmar(id), cb);
    }

    public void cancelar(String id, Consumer<QueryState<EncomendaResponse>> cb) {
        ApiQuery.execute(api().cancelar(id), cb);
    }
}
