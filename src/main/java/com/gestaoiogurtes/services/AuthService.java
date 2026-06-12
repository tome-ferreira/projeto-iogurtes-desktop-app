package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IAuthApiService;
import com.gestaoiogurtes.models.auth.LoginRequest;
import com.gestaoiogurtes.models.auth.LoginResponse;

import java.util.function.Consumer;

/**
 * Serviço de autenticação — executa o login via POST /auth/login.
 *
 * <p>Segue o padrão ApiQuery usado em todos os outros serviços da aplicação.
 * O callback {@code onStateChange} é invocado na JavaFX Application Thread
 * (via {@link com.gestaoiogurtes.api.ApiQuery}) — o controller pode
 * actualizar a UI directamente sem usar {@code Platform.runLater()}.
 *
 * <h3>Exemplo de utilização num controller</h3>
 * <pre>{@code
 * AuthService authService = new AuthService();
 *
 * authService.login(email, password, state -> {
 *     if (state.isLoading())  { btnEntrar.setDisable(true); }
 *     if (state.isSuccess())  { /* guardar sessão e navegar *\/ }
 *     if (state.isError())    { lblErro.setText(state.getErrorMessage()); }
 * });
 * }</pre>
 */
public class AuthService {

    private IAuthApiService api() {
        return RetrofitClient.getInstance().getService(IAuthApiService.class);
    }

    /**
     * Autentica o utilizador com as credenciais fornecidas.
     *
     * <p>A sequência de estados emitida é:
     * <ol>
     *   <li>{@code QueryState.loading()} — imediatamente, na thread do chamador</li>
     *   <li>{@code QueryState.success(LoginResponse)} ou
     *       {@code QueryState.error(mensagem, causa)} — na JavaFX Application Thread</li>
     * </ol>
     *
     * @param email        endereço de email do utilizador
     * @param password     palavra-passe em texto simples
     * @param onStateChange callback invocado em cada transição de estado
     */
    public void login(String email, String password,
                      Consumer<QueryState<LoginResponse>> onStateChange) {
        LoginRequest request = new LoginRequest(email, password);
        ApiQuery.execute(api().login(request), onStateChange);
    }
}
