package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.ICertificacaoApiService;
import com.gestaoiogurtes.models.certificacao.CreateCertificacaoRequest;
import com.gestaoiogurtes.models.certificacao.CertificacaoResponse;
import com.gestaoiogurtes.models.certificacao.UpdateCertificacaoRequest;
import com.gestaoiogurtes.models.PaginatedResponse;
import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

public class CertificacaoService {

    private ICertificacaoApiService api() {
        return RetrofitClient.getInstance().getService(ICertificacaoApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<CertificacaoResponse>>> cb) {
        ApiQuery.execute(api().findAllActive(page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<CertificacaoResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateCertificacaoRequest req, Consumer<QueryState<CertificacaoResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateCertificacaoRequest req, Consumer<QueryState<CertificacaoResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }
}
