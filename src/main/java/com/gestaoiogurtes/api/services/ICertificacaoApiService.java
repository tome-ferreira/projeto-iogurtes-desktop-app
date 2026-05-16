package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.certificacao.CertificacaoResponse;
import com.gestaoiogurtes.models.certificacao.CreateCertificacaoRequest;
import com.gestaoiogurtes.models.certificacao.UpdateCertificacaoRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interface Retrofit para os endpoints do recurso Certificacao.
 */
public interface ICertificacaoApiService {

    @GET("certificacoes")
    Call<List<CertificacaoResponse>> findAllActive();

    @GET("certificacoes/{id}")
    Call<CertificacaoResponse> findById(@Path("id") String id);

    @POST("certificacoes")
    Call<CertificacaoResponse> create(@Body CreateCertificacaoRequest request);

    @PUT("certificacoes/{id}")
    Call<CertificacaoResponse> update(@Path("id") String id, @Body UpdateCertificacaoRequest request);

    @DELETE("certificacoes/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
