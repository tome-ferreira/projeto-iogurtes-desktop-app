package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.utilizador.*;

import okhttp3.ResponseBody;

import java.util.function.Consumer;

public class UtilizadorService {

    private com.gestaoiogurtes.api.services.IUtilizadorApiService api() {
        return RetrofitClient.getInstance()
                .getService(com.gestaoiogurtes.api.services.IUtilizadorApiService.class);
    }

    public void getAllActive(int page, int size, Consumer<QueryState<PaginatedResponse<UserResponse>>> cb) {
        ApiQuery.execute(api().findAllActive(page, size), cb);
    }

    public void getAllInactive(int page, int size, Consumer<QueryState<PaginatedResponse<UserResponse>>> cb) {
        ApiQuery.execute(api().findAllInactive(page, size), cb);
    }

    public void getGestores(int page, int size, Consumer<QueryState<PaginatedResponse<UserResponse>>> cb) {
        ApiQuery.execute(api().findGestores(page, size), cb);
    }

    public void getClientes(int page, int size, Consumer<QueryState<PaginatedResponse<UserResponse>>> cb) {
        ApiQuery.execute(api().findClientes(page, size), cb);
    }

    public void getAdmins(int page, int size, Consumer<QueryState<PaginatedResponse<UserResponse>>> cb) {
        ApiQuery.execute(api().findAdmins(page, size), cb);
    }

    public void getFuncionarios(int page, int size, Consumer<QueryState<PaginatedResponse<UserResponse>>> cb) {
        ApiQuery.execute(api().findFuncionarios(page, size), cb);
    }

    public void deactivate(String id, Consumer<QueryState<ResponseBody>> cb) {
        ApiQuery.execute(api().softDelete(id), cb);
    }

    public void createGestor(CreateGestorRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().createGestor(req), cb);
    }

    public void updateGestor(String id, UpdateGestorRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().updateGestor(id, req), cb);
    }

    public void createCliente(CreateClienteRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().createCliente(req), cb);
    }

    public void updateCliente(String id, UpdateClienteRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().updateCliente(id, req), cb);
    }

    public void createAdmin(CreateAdminRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().createAdmin(req), cb);
    }

    public void updateAdmin(String id, UpdateAdminRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().updateAdmin(id, req), cb);
    }

    public void createFuncionarioOp(CreateFuncionarioRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().createFuncionarioOp(req), cb);
    }

    public void createFuncionarioMp(CreateFuncionarioRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().createFuncionarioMp(req), cb);
    }

    public void updateFuncionario(String id, UpdateFuncionarioRequest req, Consumer<QueryState<UserResponse>> cb) {
        ApiQuery.execute(api().updateFuncionario(id, req), cb);
    }
}
