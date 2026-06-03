package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.materiaPrima.AddFornecedorMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.CreateMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaFornecedorResponse;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
import com.gestaoiogurtes.models.materiaPrima.UpdateFornecedorMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.UpdateMateriaPrimaRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface IMateriaPrimaApiService {

    @GET("materias-primas")
    Call<PaginatedResponse<MateriaPrimaResponse>> findAll(@Query("page") int page, @Query("size") int size);

    @GET("materias-primas/{id}")
    Call<MateriaPrimaResponse> findById(@Path("id") String id);

    @POST("materias-primas")
    Call<MateriaPrimaResponse> create(@Body CreateMateriaPrimaRequest request);

    @PUT("materias-primas/{id}")
    Call<MateriaPrimaResponse> update(@Path("id") String id, @Body UpdateMateriaPrimaRequest request);

    @DELETE("materias-primas/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);

    // ── Fornecedores de matéria prima ──────────────────────────────────────

    @GET("materias-primas/{materiaId}/fornecedores")
    Call<PaginatedResponse<MateriaPrimaFornecedorResponse>> findFornecedoresByMateria(
            @Path("materiaId") String materiaId,
            @Query("page") int page,
            @Query("size") int size);

    /** GET /materias-primas/fornecedores — lista paginada de TODOS os registos matéria-fornecedor. */
    @GET("materias-primas/fornecedores")
    Call<PaginatedResponse<MateriaPrimaFornecedorResponse>> findAllFornecedores(
            @Query("page") int page,
            @Query("size") int size);

    @POST("materias-primas/{materiaId}/fornecedores")
    Call<MateriaPrimaFornecedorResponse> createFornecedor(
            @Path("materiaId") String materiaId,
            @Body AddFornecedorMateriaPrimaRequest request);

    @PUT("materias-primas/fornecedores/{id}")
    Call<MateriaPrimaFornecedorResponse> updateFornecedor(
            @Path("id") String id,
            @Body UpdateFornecedorMateriaPrimaRequest request);

    @DELETE("materias-primas/fornecedores/{id}")
    Call<ResponseBody> softDeleteFornecedor(@Path("id") String id);
}
