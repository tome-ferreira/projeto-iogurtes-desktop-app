package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.models.empresa.CreateEmpresaRequest;
import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.models.empresa.UpdateEmpresaRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

public class EmpresaService {

    private com.gestaoiogurtes.api.services.IEmpresaApiService api() {
        return RetrofitClient.getInstance()
                .getService(com.gestaoiogurtes.api.services.IEmpresaApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<EmpresaResponse>>> onStateChange) {
        ApiQuery.execute(api().findAll(page, size), onStateChange);
    }

    public void getById(String id, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().findById(id), onStateChange);
    }

    public void create(CreateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().create(request), onStateChange);
    }

    public void update(String id, UpdateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().update(id, request), onStateChange);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> onStateChange) {
        ApiQuery.execute(api().softDelete(id), onStateChange);
    }
}
