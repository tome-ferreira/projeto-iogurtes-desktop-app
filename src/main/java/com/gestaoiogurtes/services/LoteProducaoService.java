package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.ILoteProducaoApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoResponse;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoDetalheResponse;

import java.util.function.Consumer;

public class LoteProducaoService {

    private ILoteProducaoApiService api() {
        return RetrofitClient.getInstance()
                .getService(ILoteProducaoApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<LoteProducaoResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size, "createdAt", "desc"), cb);
    }

    public void getById(String id, Consumer<QueryState<LoteProducaoDetalheResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }
}
