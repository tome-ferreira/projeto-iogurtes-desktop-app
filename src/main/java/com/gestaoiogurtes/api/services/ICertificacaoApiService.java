package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.certificacao.CertificacaoResponse;
import com.gestaoiogurtes.models.certificacao.CreateCertificacaoRequest;
import com.gestaoiogurtes.models.certificacao.UpdateCertificacaoRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ICertificacaoApiService {

    @GET("certificacoes/active")
    Call<PaginatedResponse<CertificacaoResponse>> findAllActive(
            @Query("page") int page,
            @Query("size") int size);

    @GET("certificacoes/{id}")
    Call<CertificacaoResponse> findById(@Path("id") String id);

    @POST("certificacoes")
    Call<CertificacaoResponse> create(@Body CreateCertificacaoRequest request);

    @PUT("certificacoes/{id}")
    Call<CertificacaoResponse> update(@Path("id") String id, @Body UpdateCertificacaoRequest request);

    @DELETE("certificacoes/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
