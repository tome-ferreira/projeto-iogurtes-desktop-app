package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.tipoPallet.CreateTipoPalletRequest;
import com.gestaoiogurtes.models.tipoPallet.TipoPalletResponse;
import com.gestaoiogurtes.models.tipoPallet.UpdateTipoPalletRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ITipoPalletApiService {

    @GET("pallet-tipos")
    Call<PaginatedResponse<TipoPalletResponse>> findAllActive(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("direction") String direction
    );

    @GET("pallet-tipos/{id}")
    Call<TipoPalletResponse> findById(@Path("id") String id);

    @POST("pallet-tipos")
    Call<TipoPalletResponse> create(@Body CreateTipoPalletRequest request);

    @PUT("pallet-tipos/{id}")
    Call<TipoPalletResponse> update(@Path("id") String id, @Body UpdateTipoPalletRequest request);

    @DELETE("pallet-tipos/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
