package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IAuthApiService;
import com.gestaoiogurtes.models.auth.LoginRequest;
import com.gestaoiogurtes.models.auth.LoginResponse;

import java.util.function.Consumer;

public class AuthService {

    private IAuthApiService api() {
        return RetrofitClient.getInstance().getService(IAuthApiService.class);
    }

    public void login(String email, String password,
            Consumer<QueryState<LoginResponse>> onStateChange) {
        LoginRequest request = new LoginRequest(email, password);
        ApiQuery.execute(api().login(request), onStateChange);
    }
}
