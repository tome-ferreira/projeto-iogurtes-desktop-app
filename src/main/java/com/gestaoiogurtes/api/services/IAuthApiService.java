package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.auth.LoginRequest;
import com.gestaoiogurtes.models.auth.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface Retrofit para o endpoint de autenticação.
 *
 * <p>O endpoint {@code /auth/login} está marcado como público no backend
 * (SecurityConfig.permitAll) — não requer header Authorization.
 */
public interface IAuthApiService {

    /**
     * Autentica um utilizador e devolve um JWT.
     *
     * <p>POST /auth/login — único endpoint público da API.
     *
     * @param request corpo com {@code email} e {@code password}
     * @return chamada Retrofit com {@link LoginResponse}
     */
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
