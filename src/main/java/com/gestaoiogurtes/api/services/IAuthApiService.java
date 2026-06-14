package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.auth.LoginRequest;
import com.gestaoiogurtes.models.auth.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAuthApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
