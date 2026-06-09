package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.encomenda.EncomendaDetalheResponse;
import com.gestaoiogurtes.models.encomenda.PageEncomendaResponse;
import com.gestaoiogurtes.models.encomenda.EncomendaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IEncomendaApiService {

    @GET("encomendas")
    Call<PageEncomendaResponse> findAll(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("encomendas/estado/{estado}")
    Call<PageEncomendaResponse> findByEstado(
            @Path("estado") String estado,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("encomendas/{id}")
    Call<EncomendaDetalheResponse> findById(@Path("id") String id);

    @PATCH("encomendas/{id}/confirmar")
    Call<EncomendaResponse> confirmar(@Path("id") String id);

    @PATCH("encomendas/{id}/cancelar")
    Call<EncomendaResponse> cancelar(@Path("id") String id);

}
