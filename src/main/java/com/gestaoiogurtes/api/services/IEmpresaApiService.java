package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.models.empresa.CreateEmpresaRequest;
import com.gestaoiogurtes.models.empresa.UpdateEmpresaRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface IEmpresaApiService {

    @GET("empresas")
    Call<PaginatedResponse<EmpresaResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size);

    @GET("empresas/{id}")
    Call<EmpresaResponse> findById(@Path("id") String id);

    @POST("empresas")
    Call<EmpresaResponse> create(@Body CreateEmpresaRequest request);

    @PUT("empresas/{id}")
    Call<EmpresaResponse> update(@Path("id") String id, @Body UpdateEmpresaRequest request);

    @DELETE("empresas/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
