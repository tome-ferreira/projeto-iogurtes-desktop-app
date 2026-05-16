package com.gestaoiogurtes.api.services;

import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.models.empresa.CreateEmpresaRequest;
import com.gestaoiogurtes.models.empresa.UpdateEmpresaRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interface Retrofit para os endpoints do recurso Empresa.
 *
 * <p>
 * Paths baseados em {@code GET /v3/api-docs} (tag {@code empresa-controller}):
 * </p>
 * <ul>
 * <li>GET /empresas → listar todas</li>
 * <li>GET /empresas/{id} → obter por ID</li>
 * <li>POST /empresas → criar nova empresa</li>
 * <li>PUT /empresas/{id} → actualizar empresa</li>
 * <li>DELETE /empresas/{id} → soft-delete</li>
 * </ul>
 *
 * <p>
 * Utilização via {@link com.gestaoiogurtes.api.RetrofitClient}:
 * </p>
 * 
 * <pre>{@code
 * IEmpresaApiService api = RetrofitClient.getInstance().getService(IEmpresaApiService.class);
 * }</pre>
 */
public interface IEmpresaApiService {

    /** Devolve todas as empresas activas. */
    @GET("empresas")
    Call<List<EmpresaResponse>> findAll();

    /** Devolve uma empresa pelo seu UUID. */
    @GET("empresas/{id}")
    Call<EmpresaResponse> findById(@Path("id") String id);

    /** Cria uma nova empresa. */
    @POST("empresas")
    Call<EmpresaResponse> create(@Body CreateEmpresaRequest request);

    /** Actualiza os dados de uma empresa existente. */
    @PUT("empresas/{id}")
    Call<EmpresaResponse> update(@Path("id") String id, @Body UpdateEmpresaRequest request);

    /** Executa o soft-delete de uma empresa. */
    @DELETE("empresas/{id}")
    Call<ResponseBody> softDelete(@Path("id") String id);
}
