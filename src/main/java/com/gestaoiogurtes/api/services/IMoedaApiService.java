package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.moeda.MoedaResponse;
import com.gestaoiogurtes.models.moeda.CreateMoedaRequest;
import com.gestaoiogurtes.models.moeda.UpdateMoedaRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface IMoedaApiService {

    @GET("moedas")
    Call<PaginatedResponse<MoedaResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("moedas/{id}")
    Call<MoedaResponse> findById(@Path("id") String id);

    @POST("moedas")
    Call<MoedaResponse> create(@Body CreateMoedaRequest request);

    @PUT("moedas/{id}")
    Call<MoedaResponse> update(@Path("id") String id, @Body UpdateMoedaRequest request);

    @DELETE("moedas/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
