package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoResponse;
import com.gestaoiogurtes.models.loteProducao.LoteProducaoDetalheResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ILoteProducaoApiService {

    @GET("lotes-producao")
    Call<PaginatedResponse<LoteProducaoResponse>> findAll(@Query("page") int page, @Query("size") int size, @Query("sort") String sort, @Query("direction") String direction);

    @GET("lotes-producao/{id}")
    Call<LoteProducaoDetalheResponse> findById(@Path("id") String id);
}
