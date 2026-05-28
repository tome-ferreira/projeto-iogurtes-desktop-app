package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IFornecedorApiService;
import com.gestaoiogurtes.models.certificacao.CertificacaoResponse;
import com.gestaoiogurtes.models.fornecedor.AddCertificacaoRequest;
import com.gestaoiogurtes.models.fornecedor.CreateFornecedorRequest;
import com.gestaoiogurtes.models.fornecedor.FornecedorCertificacaoResponse;
import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import com.gestaoiogurtes.models.fornecedor.UpdateCertificacaoRequest;
import com.gestaoiogurtes.models.fornecedor.UpdateFornecedorRequest;
import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.models.PaginatedResponse;
import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class FornecedorService {

    private IFornecedorApiService api() {
        return RetrofitClient.getInstance().getService(IFornecedorApiService.class);
    }

    // ── Fornecedores ────────────────────────────────────────────────────────

    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    public void getById(String id, Consumer<QueryState<FornecedorResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    public void getByTipo(String tipoId, int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorResponse>>> cb) {
        ApiQuery.execute(api().findAllByTipo(tipoId, page, size), cb);
    }

    public void create(CreateFornecedorRequest req, Consumer<QueryState<FornecedorResponse>> cb) {
        ApiQuery.execute(api().create(req), cb);
    }

    public void update(String id, UpdateFornecedorRequest req, Consumer<QueryState<FornecedorResponse>> cb) {
        ApiQuery.execute(api().update(id, req), cb);
    }

    public void delete(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }

    // ── Tipos de Fornecedor ─────────────────────────────────────────────────

    public void getAllTipos(int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorTipoResponse>>> cb) {
        ApiQuery.execute(api().findAllTipos(page, size), cb);
    }

    // ── Certificações de Fornecedor ─────────────────────────────────────────

    public void getAllCertificacoes(int page, int size, Consumer<QueryState<PaginatedResponse<FornecedorCertificacaoResponse>>> cb) {
        ApiQuery.execute(api().findAllCertificacoes(page, size), cb);
    }

    public void addCertificacao(String fornecedorId, AddCertificacaoRequest req, Consumer<QueryState<FornecedorCertificacaoResponse>> cb) {
        ApiQuery.execute(api().addCertificacao(fornecedorId, req), cb);
    }

    public void updateCertificacao(String fornecedorCertificacaoId, UpdateCertificacaoRequest req, Consumer<QueryState<FornecedorCertificacaoResponse>> cb) {
        ApiQuery.execute(api().updateCertificacao(fornecedorCertificacaoId, req), cb);
    }

    public void removeCertificacao(String fornecedorCertificacaoId, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().removeCertificacao(fornecedorCertificacaoId), cb);
    }

    // ── Certificações (catálogo) ────────────────────────────────────────────

    public void getAllCertificacoesDisponiveis(int page, int size, Consumer<QueryState<PaginatedResponse<CertificacaoResponse>>> cb) {
        ApiQuery.execute(api().findAllCertificacoesDisponiveis(page, size), cb);
    }
}
