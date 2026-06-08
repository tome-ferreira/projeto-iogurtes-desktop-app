package com.gestaoiogurtes.api.services;

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
import retrofit2.Call;
import retrofit2.http.*;

public interface IFornecedorApiService {

    // ── Fornecedores ────────────────────────────────────────────────────────

    @GET("fornecedor")
    Call<PaginatedResponse<FornecedorResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("fornecedor/{id}")
    Call<FornecedorResponse> findById(@Path("id") String id);

    @GET("fornecedor/tipo/{tipoId}")
    Call<PaginatedResponse<FornecedorResponse>> findAllByTipo(
            @Path("tipoId") String tipoId,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("fornecedor")
    Call<FornecedorResponse> create(@Body CreateFornecedorRequest request);

    @PUT("fornecedor/{id}")
    Call<FornecedorResponse> update(@Path("id") String id, @Body UpdateFornecedorRequest request);

    @DELETE("fornecedor/{id}")
    Call<ResponseBody> delete(@Path("id") String id);

    // ── Tipos de Fornecedor ─────────────────────────────────────────────────

    @GET("fornecedor-tipos")
    Call<PaginatedResponse<FornecedorTipoResponse>> findAllTipos(
            @Query("page") int page,
            @Query("size") int size
    );

    // ── Certificações de Fornecedor ─────────────────────────────────────────

    /**
     * GET /fornecedores-certificacoes
     * Nota: A API não suporta filtro por fornecedorId neste endpoint (sem query param).
     * TODO: Adicionar filtro por fornecedorId quando a API suportar (e.g. @Query("fornecedorId")).
     * Por agora, a filtragem é feita no cliente após receber todos os resultados.
     */
    @GET("fornecedores-certificacoes")
    Call<PaginatedResponse<FornecedorCertificacaoResponse>> findAllCertificacoes(
            @Query("page") int page,
            @Query("size") int size
    );

    /** POST /fornecedor/{fornecedorId}/certificacoes */
    @POST("fornecedor/{fornecedorId}/certificacoes")
    Call<FornecedorCertificacaoResponse> addCertificacao(
            @Path("fornecedorId") String fornecedorId,
            @Body AddCertificacaoRequest request
    );

    /** PUT /fornecedor/certificacoes/{fornecedorCertificacaoId} */
    @PUT("fornecedor/certificacoes/{fornecedorCertificacaoId}")
    Call<FornecedorCertificacaoResponse> updateCertificacao(
            @Path("fornecedorCertificacaoId") String fornecedorCertificacaoId,
            @Body UpdateCertificacaoRequest request
    );

    /** DELETE /fornecedor/certificacoes/{fornecedorCertificacaoId} */
    @DELETE("fornecedor/certificacoes/{fornecedorCertificacaoId}")
    Call<ResponseBody> removeCertificacao(
            @Path("fornecedorCertificacaoId") String fornecedorCertificacaoId
    );

    // ── Certificações (catálogo) ────────────────────────────────────────────

    /** GET /certificacoes — lista paginada de certificações disponíveis */
    @GET("certificacoes")
    Call<PaginatedResponse<CertificacaoResponse>> findAllCertificacoesDisponiveis(
            @Query("page") int page,
            @Query("size") int size
    );
}
