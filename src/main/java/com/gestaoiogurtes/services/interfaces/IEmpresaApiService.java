package com.gestaoiogurtes.services.interfaces;

import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.model.CreateEmpresaRequest;
import com.gestaoiogurtes.model.EmpresaResponse;
import com.gestaoiogurtes.model.UpdateEmpresaRequest;

import okhttp3.ResponseBody;

import java.util.List;
import java.util.function.Consumer;

/**
 * Contrato assíncrono para operações sobre o recurso Empresa.
 *
 * <p>
 * Todos os métodos recebem um {@code Consumer<QueryState<T>>} que é
 * invocado em cada transição de estado (LOADING → SUCCESS | ERROR).
 * Os callbacks são sempre chamados na JavaFX Application Thread.
 * </p>
 *
 * <p>
 * Exemplo de utilização num controller:
 * </p>
 *
 * <pre>{@code
 * empresaService.getAll(state -> {
 *     switch (state.getStatus()) {
 *         case LOADING -> setLoading(true);
 *         case SUCCESS -> {
 *             setLoading(false);
 *             tabela.setAll(state.getData());
 *         }
 *         case ERROR -> {
 *             setLoading(false);
 *             mostrarErro(state.getErrorMessage());
 *         }
 *     }
 * });
 * }</pre>
 */
public interface IEmpresaApiService {

    /**
     * Obtém todas as empresas activas.
     *
     * @param onStateChange callback com a lista de empresas em
     *                      {@code state.getData()}
     */
    void getAll(Consumer<QueryState<List<EmpresaResponse>>> onStateChange);

    /**
     * Obtém uma empresa pelo seu UUID.
     *
     * @param id            UUID da empresa (formato String)
     * @param onStateChange callback com a empresa em {@code state.getData()}
     */
    void getById(String id, Consumer<QueryState<EmpresaResponse>> onStateChange);

    /**
     * Cria uma nova empresa.
     *
     * @param request       dados da nova empresa
     * @param onStateChange callback com a empresa criada em {@code state.getData()}
     */
    void create(CreateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange);

    /**
     * Actualiza uma empresa existente.
     *
     * @param id            UUID da empresa a actualizar
     * @param request       novos dados da empresa
     * @param onStateChange callback com a empresa actualizada em
     *                      {@code state.getData()}
     */
    void update(String id, UpdateEmpresaRequest request, Consumer<QueryState<EmpresaResponse>> onStateChange);

    /**
     * Executa o soft-delete de uma empresa.
     *
     * @param id            UUID da empresa a eliminar
     * @param onStateChange callback com mensagem de confirmação em
     *                      {@code state.getData()}
     */
    void delete(String id, Consumer<QueryState<ResponseBody>> onStateChange);
}
