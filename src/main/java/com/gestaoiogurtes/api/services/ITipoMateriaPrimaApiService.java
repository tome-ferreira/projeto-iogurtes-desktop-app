package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.tipoMateriaPrima.CreateTipoMateriaPrimaRequest;
import com.gestaoiogurtes.models.tipoMateriaPrima.TipoMateriaPrimaResponse;
import com.gestaoiogurtes.models.tipoMateriaPrima.UpdateTipoMateriaPrimaRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ITipoMateriaPrimaApiService {

    @GET("tipos-materia")
    Call<PaginatedResponse<TipoMateriaPrimaResponse>> findAll(@Query("page") int page, @Query("size") int size);

    @GET("tipos-materia/{id}")
    Call<TipoMateriaPrimaResponse> findById(@Path("id") String id);

    @POST("tipos-materia")
    Call<TipoMateriaPrimaResponse> create(@Body CreateTipoMateriaPrimaRequest request);

    @PUT("tipos-materia/{id}")
    Call<TipoMateriaPrimaResponse> update(@Path("id") String id, @Body UpdateTipoMateriaPrimaRequest request);

    @DELETE("tipos-materia/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
