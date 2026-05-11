package com.gestaoiogurtes.services.real;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.model.CreateEmpresaRequest;
import com.gestaoiogurtes.model.EmpresaResponse;
import com.gestaoiogurtes.model.UpdateEmpresaRequest;

import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implementação real do serviço Empresa que comunica com o backend via
 * Retrofit.
 *
 * <p>
 * Cada método segue o mesmo padrão:
 * </p>
 * <ol>
 * <li>Obtém a interface Retrofit via {@link RetrofitClient}.</li>
 * <li>Cria o {@code Call} (pedido não enviado).</li>
 * <li>Delega a execução ao {@link ApiQuery#execute(Call, Consumer)}.</li>
 * </ol>
 *
 * <p>
 * <strong>Regra:</strong> nunca chamar {@code Platform.runLater()} aqui.
 * O {@code ApiQuery} já garante que os callbacks chegam na JavaFX Application
 * Thread.
 * </p>
 */
public class RealEmpresaService implements com.gestaoiogurtes.services.interfaces.IEmpresaApiService {

    /** Devolve o proxy Retrofit (criado lazy via RetrofitClient singleton). */
    private com.gestaoiogurtes.api.services.IEmpresaApiService api() {
        return RetrofitClient.getInstance()
                .getService(com.gestaoiogurtes.api.services.IEmpresaApiService.class);
    }

    @Override
    public void getAll(Consumer<QueryState<List<EmpresaResponse>>> onStateChange) {
        ApiQuery.execute(api().findAll(), onStateChange);
    }

    @Override
    public void getById(String id, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().findById(id), onStateChange);
    }

    @Override
    public void create(CreateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().create(request), onStateChange);
    }

    @Override
    public void update(String id, UpdateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().update(id, request), onStateChange);
    }

    @Override
    public void delete(String id, Consumer<QueryState<ResponseBody>> onStateChange) {
        ApiQuery.execute(api().softDelete(id), onStateChange);
    }
}
