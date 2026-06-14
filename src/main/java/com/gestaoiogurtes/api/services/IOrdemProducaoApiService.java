package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.ordemProducao.CreateOrdemProducaoRequest;
import com.gestaoiogurtes.models.ordemProducao.OrdemProducaoResponse;
import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IOrdemProducaoApiService {

    @GET("ordens-producao")
    Call<PaginatedResponse<OrdemProducaoResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("ordens-producao/estado/{estado}")
    Call<PaginatedResponse<OrdemProducaoResponse>> findByEstado(
            @Path("estado") String estado,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("ordens-producao/{id}")
    Call<OrdemProducaoResponse> findById(@Path("id") String id);

    @POST("ordens-producao")
    Call<OrdemProducaoResponse> create(@Body CreateOrdemProducaoRequest request);

    @GET("produtos-finais")
    Call<PaginatedResponse<ProdutoFinalResponse>> findAllProdutosFinais(
            @Query("page") int page,
            @Query("size") int size
    );

    @PATCH("ordens-producao/{id}/aprovar")
    Call<OrdemProducaoResponse> aprovar(@Path("id") String id);

    @PATCH("ordens-producao/{id}/cancelar")
    Call<OrdemProducaoResponse> cancelar(@Path("id") String id);

    @PATCH("ordens-producao/{id}/concluir")
    Call<OrdemProducaoResponse> concluir(@Path("id") String id);
}
