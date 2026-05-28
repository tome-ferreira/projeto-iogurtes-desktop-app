package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.fornecedor.CreateFornecedorRequest;
import com.gestaoiogurtes.models.fornecedor.FornecedorResponse;
import com.gestaoiogurtes.models.fornecedor.UpdateFornecedorRequest;
import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.models.PaginatedResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface IFornecedorApiService {

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
    Call<ResponseBody> softDelete(@Path("id") String id);

    @GET("fornecedor-tipos")
    Call<PaginatedResponse<FornecedorTipoResponse>> findAllTipos(
            @Query("page") int page,
            @Query("size") int size
    );
}
