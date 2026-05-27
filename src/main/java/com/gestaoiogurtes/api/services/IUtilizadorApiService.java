package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.utilizador.UserResponse;
import com.gestaoiogurtes.models.utilizador.CreateGestorRequest;
import com.gestaoiogurtes.models.utilizador.UpdateGestorRequest;
import com.gestaoiogurtes.models.utilizador.CreateClienteRequest;
import com.gestaoiogurtes.models.utilizador.UpdateClienteRequest;
import com.gestaoiogurtes.models.utilizador.CreateAdminRequest;
import com.gestaoiogurtes.models.utilizador.UpdateAdminRequest;
import com.gestaoiogurtes.models.utilizador.CreateFuncionarioRequest;
import com.gestaoiogurtes.models.utilizador.UpdateFuncionarioRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interface Retrofit para os endpoints do recurso User (user-controller).
 *
 * Paths baseados em GET /v3/api-docs (tag user-controller):
 *   GET  /users/active          → listar utilizadores ativos (paginado)
 *   GET  /users/inactive        → listar utilizadores inativos (paginado)
 *   GET  /users/{id}            → obter por ID
 *   DELETE /users/{id}          → soft-delete (desativar)
 *   POST /users/gestores        → criar Gestor
 *   PUT  /users/gestores/{id}   → atualizar Gestor
 *   POST /users/clientes        → criar Cliente
 *   PUT  /users/clientes/{id}   → atualizar Cliente
 *   POST /users/admins          → criar Admin
 *   PUT  /users/admins/{id}     → atualizar Admin
 *   POST /users/funcionarios/op → criar Funcionário OP
 *   POST /users/funcionarios/mp → criar Funcionário MP
 *   PUT  /users/funcionarios/{id} → atualizar Funcionário (OP ou MP)
 */
public interface IUtilizadorApiService {

    @GET("users/active")
    Call<PaginatedResponse<UserResponse>> findAllActive(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/inactive")
    Call<PaginatedResponse<UserResponse>> findAllInactive(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/gestores")
    Call<PaginatedResponse<UserResponse>> findGestores(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/clientes")
    Call<PaginatedResponse<UserResponse>> findClientes(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/admins")
    Call<PaginatedResponse<UserResponse>> findAdmins(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/funcionarios")
    Call<PaginatedResponse<UserResponse>> findFuncionarios(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("users/{id}")
    Call<UserResponse> findById(@Path("id") String id);

    @DELETE("users/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);

    /* ── Gestores ─────────────────────────────────────────────── */

    @POST("users/gestores")
    Call<UserResponse> createGestor(@Body CreateGestorRequest request);

    @PUT("users/gestores/{id}")
    Call<UserResponse> updateGestor(@Path("id") String id, @Body UpdateGestorRequest request);

    /* ── Clientes ─────────────────────────────────────────────── */

    @POST("users/clientes")
    Call<UserResponse> createCliente(@Body CreateClienteRequest request);

    @PUT("users/clientes/{id}")
    Call<UserResponse> updateCliente(@Path("id") String id, @Body UpdateClienteRequest request);

    /* ── Admins ────────────────────────────────────────────────── */

    @POST("users/admins")
    Call<UserResponse> createAdmin(@Body CreateAdminRequest request);

    @PUT("users/admins/{id}")
    Call<UserResponse> updateAdmin(@Path("id") String id, @Body UpdateAdminRequest request);

    /* ── Funcionários OP ──────────────────────────────────────── */

    @POST("users/funcionarios/op")
    Call<UserResponse> createFuncionarioOp(@Body CreateFuncionarioRequest request);

    /* ── Funcionários MP ──────────────────────────────────────── */

    @POST("users/funcionarios/mp")
    Call<UserResponse> createFuncionarioMp(@Body CreateFuncionarioRequest request);

    /* ── Funcionários (update common) ─────────────────────────── */

    @PUT("users/funcionarios/{id}")
    Call<UserResponse> updateFuncionario(@Path("id") String id, @Body UpdateFuncionarioRequest request);
}
