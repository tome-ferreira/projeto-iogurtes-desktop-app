package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.fornecedortipo.FornecedorTipoResponse;
import com.gestaoiogurtes.models.fornecedortipo.CreateFornecedorTipoRequest;
import com.gestaoiogurtes.models.fornecedortipo.UpdateFornecedorTipoRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface IFornecedorTipoApiService {

    @GET("fornecedor-tipos")
    Call<PaginatedResponse<FornecedorTipoResponse>> findAllActive(
            @Query("page") int page,
            @Query("size") int size);

    @GET("fornecedor-tipos/{id}")
    Call<FornecedorTipoResponse> findById(@Path("id") String id);

    @POST("fornecedor-tipos")
    Call<FornecedorTipoResponse> create(@Body CreateFornecedorTipoRequest request);

    @PUT("fornecedor-tipos/{id}")
    Call<FornecedorTipoResponse> update(@Path("id") String id, @Body UpdateFornecedorTipoRequest request);

    @DELETE("fornecedor-tipos/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
