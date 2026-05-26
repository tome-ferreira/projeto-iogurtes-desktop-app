package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.models.empresa.CreateEmpresaRequest;
import com.gestaoiogurtes.models.empresa.EmpresaResponse;
import com.gestaoiogurtes.models.empresa.UpdateEmpresaRequest;
import com.gestaoiogurtes.models.PaginatedResponse;

import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

/**
 * Serviço Empresa que comunica com o backend via Retrofit.
 *
 * <p>
 * Cada método segue o mesmo padrão:
 * </p>
 * <ol>
 * <li>Obtém a interface Retrofit via {@link RetrofitClient}.</li>
 * <li>Cria o {@code Call} (pedido não enviado).</li>
 * <li>Delega a execução ao {@link ApiQuery#execute(retrofit2.Call, Consumer)}.</li>
 * </ol>
 *
 * <p>
 * <strong>Regra:</strong> nunca chamar {@code Platform.runLater()} aqui.
 * O {@code ApiQuery} já garante que os callbacks chegam na JavaFX Application
 * Thread.
 * </p>
 */
public class EmpresaService {

    /** Devolve o proxy Retrofit (criado lazy via RetrofitClient singleton). */
    private com.gestaoiogurtes.api.services.IEmpresaApiService api() {
        return RetrofitClient.getInstance()
                .getService(com.gestaoiogurtes.api.services.IEmpresaApiService.class);
    }

    /**
     * Obtém as empresas activas com paginação.
     *
     * @param page          página actual (0-indexed)
     * @param size          tamanho da página
     * @param onStateChange callback com a resposta paginada
     */
    public void getAll(int page, int size, Consumer<QueryState<PaginatedResponse<EmpresaResponse>>> onStateChange) {
        ApiQuery.execute(api().findAll(page, size), onStateChange);
    }

    /**
     * Obtém uma empresa pelo seu UUID.
     *
     * @param id            UUID da empresa
     * @param onStateChange callback com a empresa
     */
    public void getById(String id, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().findById(id), onStateChange);
    }

    /**
     * Cria uma nova empresa.
     *
     * @param request       dados da nova empresa
     * @param onStateChange callback com a empresa criada
     */
    public void create(CreateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().create(request), onStateChange);
    }

    /**
     * Actualiza uma empresa existente.
     *
     * @param id            UUID da empresa a actualizar
     * @param request       novos dados da empresa
     * @param onStateChange callback com a empresa actualizada
     */
    public void update(String id, UpdateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange) {
        ApiQuery.execute(api().update(id, request), onStateChange);
    }

    /**
     * Executa o soft-delete de uma empresa.
     *
     * @param id            UUID da empresa a eliminar
     * @param onStateChange callback com mensagem de confirmação
     */
    public void delete(String id, Consumer<QueryState<ResponseBody>> onStateChange) {
        ApiQuery.execute(api().softDelete(id), onStateChange);
    }
}
