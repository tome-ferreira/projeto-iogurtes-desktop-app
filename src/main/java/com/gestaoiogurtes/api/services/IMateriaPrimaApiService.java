package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.materiaPrima.CreateMateriaPrimaRequest;
import com.gestaoiogurtes.models.materiaPrima.MateriaPrimaResponse;
import com.gestaoiogurtes.models.materiaPrima.UpdateMateriaPrimaRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface IMateriaPrimaApiService {

    @GET("materias-primas")
    Call<PaginatedResponse<MateriaPrimaResponse>> findAll(@Query("page") int page, @Query("size") int size);

    @GET("materias-primas/{id}")
    Call<MateriaPrimaResponse> findById(@Path("id") String id);

    @POST("materias-primas")
    Call<MateriaPrimaResponse> create(@Body CreateMateriaPrimaRequest request);

    @PUT("materias-primas/{id}")
    Call<MateriaPrimaResponse> update(@Path("id") String id, @Body UpdateMateriaPrimaRequest request);

    @DELETE("materias-primas/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
