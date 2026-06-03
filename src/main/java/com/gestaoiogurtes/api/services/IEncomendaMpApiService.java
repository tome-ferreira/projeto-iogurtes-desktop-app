package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.encomendaMp.CreateEncomendaMpRequest;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface Retrofit para o encomenda-mp-controller.
 * Paths sem barra inicial — Retrofit concatena à BASE_URL.
 */
public interface IEncomendaMpApiService {

    /** GET /encomendas-mp — lista paginada de todas as encomendas MP. */
    @GET("encomendas-mp")
    Call<PaginatedResponse<EncomendaMpResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size
    );

    /** GET /encomendas-mp/estado/{estado} — lista paginada filtrada por estado.
     *  Valores possíveis de estado: PENDENTE, ENCOMENDADA, RECEBIDA, CANCELADA
     */
    @GET("encomendas-mp/estado/{estado}")
    Call<PaginatedResponse<EncomendaMpResponse>> findByEstado(
            @Path("estado") String estado,
            @Query("page") int page,
            @Query("size") int size
    );

    /** GET /encomendas-mp/{id} — detalhe completo de uma encomenda MP. */
    @GET("encomendas-mp/{id}")
    Call<EncomendaMpResponse> findById(@Path("id") String id);

    /** POST /encomendas-mp — criar nova encomenda MP. */
    @POST("encomendas-mp")
    Call<EncomendaMpResponse> create(@Body CreateEncomendaMpRequest request);
}
