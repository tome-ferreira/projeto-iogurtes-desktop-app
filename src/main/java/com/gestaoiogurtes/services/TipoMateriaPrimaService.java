package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.ITipoMateriaPrimaApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.tipoMateriaPrima.CreateTipoMateriaPrimaRequest;
import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import com.gestaoiogurtes.models.tipoMateriaPrima.UpdateTipoMateriaPrimaRequest;
import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class TipoMateriaPrimaService {

    private ITipoMateriaPrimaApiService api() {
        return RetrofitClient.getInstance().getService(ITipoMateriaPrimaApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<TipoMateriaPrimaResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<TipoMateriaPrimaResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateTipoMateriaPrimaRequest req, Consumer<QueryState<TipoMateriaPrimaResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateTipoMateriaPrimaRequest req, Consumer<QueryState<TipoMateriaPrimaResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }
}
