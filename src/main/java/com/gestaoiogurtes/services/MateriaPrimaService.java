package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IMateriaPrimaApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.materiaPrima.AddFornecedorMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.CreateMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaFornecedorResponse;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
import com.gestaoiogurtes.models.materiaPrima.UpdateFornecedorMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.UpdateMateriaPrimaRequest;
import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class MateriaPrimaService {

    private IMateriaPrimaApiService api() {
        return RetrofitClient.getInstance()
                .getService(IMateriaPrimaApiService.class);
    }

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<MateriaPrimaResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<MateriaPrimaResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void create(CreateMateriaPrimaRequest req, Consumer<QueryState<MateriaPrimaResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateMateriaPrimaRequest req, Consumer<QueryState<MateriaPrimaResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }

    // ── Fornecedores de matéria prima ──────────────────────────────────────

    public void getFornecedores(String materiaId, int page, int size,
                                Consumer<QueryState<PaginatedResponse<MateriaPrimaFornecedorResponse>>> cb) {
        ApiQuery.execute(api().findFornecedoresByMateria(materiaId, page, size), cb);
    }

    public void addFornecedor(String materiaId, AddFornecedorMateriaPrimaRequest req,
                              Consumer<QueryState<MateriaPrimaFornecedorResponse>> cb) {
        ApiQuery.execute(api().createFornecedor(materiaId, req), cb);
    }

    public void updateFornecedor(String id, UpdateFornecedorMateriaPrimaRequest req,
                                 Consumer<QueryState<MateriaPrimaFornecedorResponse>> cb) {
        ApiQuery.execute(api().updateFornecedor(id, req), cb);
    }

    public void deleteFornecedor(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDeleteFornecedor(id), cb);
    }
}
