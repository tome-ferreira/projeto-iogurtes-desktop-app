package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.produtoFinal.CreateProdutoFinalRequest;
import com.gestaoiogurtes.models.produtoFinal.ProdutoFinalResponse;
import com.gestaoiogurtes.models.produtoFinal.UpdateProdutoFinalRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Mapeamento Retrofit dos endpoints do produto-final-controller.
 * Paths sem "/" inicial — o Retrofit concatena à BASE_URL.
 */
public interface IProdutoFinalApiService {

    @GET("produtos-finais")
    Call<PaginatedResponse<ProdutoFinalResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size);

    @GET("produtos-finais/{id}")
    Call<ProdutoFinalResponse> findById(@Path("id") String id);

    @POST("produtos-finais")
    Call<ProdutoFinalResponse> create(@Body CreateProdutoFinalRequest request);

    @PUT("produtos-finais/{id}")
    Call<ProdutoFinalResponse> update(@Path("id") String id, @Body UpdateProdutoFinalRequest request);

    @DELETE("produtos-finais/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);

    @POST("produtos-finais/{produtoId}/composicao")
    Call<ProdutoFinalResponse> addMateriasComposicao(
            @Path("produtoId") String produtoId,
            @Body com.gestaoiogurtes.models.produtoFinal.AddMateriasComposicaoRequest request);

    @DELETE("produtos-finais/{produtoId}/composicao/{composicaoId}")
    Call<ResponseBody> removeComposicao(
            @Path("produtoId") String produtoId,
            @Path("composicaoId") String composicaoId);

}
