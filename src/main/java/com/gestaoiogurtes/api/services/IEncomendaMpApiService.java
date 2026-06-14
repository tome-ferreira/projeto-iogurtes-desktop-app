package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.encomendaMp.CreateEncomendaMpRequest;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IEncomendaMpApiService {

        @GET("encomendas-mp")
        Call<PaginatedResponse<EncomendaMpResponse>> findAll(
                        @Query("page") int page,
                        @Query("size") int size);

        @GET("encomendas-mp/estado/{estado}")
        Call<PaginatedResponse<EncomendaMpResponse>> findByEstado(
                        @Path("estado") String estado,
                        @Query("page") int page,
                        @Query("size") int size);

        @GET("encomendas-mp/{id}")
        Call<EncomendaMpResponse> findById(@Path("id") String id);

        @POST("encomendas-mp")
        Call<EncomendaMpResponse> create(@Body CreateEncomendaMpRequest request);

        @PATCH("encomendas-mp/{id}/aprovar")
        Call<EncomendaMpResponse> aprovar(@Path("id") String id);

        @PATCH("encomendas-mp/{id}/cancelar")
        Call<EncomendaMpResponse> cancelar(@Path("id") String id);

        @PATCH("encomendas-mp/{id}/recebida")
        Call<EncomendaMpResponse> marcarRecebida(@Path("id") String id);
}
