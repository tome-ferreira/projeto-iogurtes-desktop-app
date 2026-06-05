package com.gestaoiogurtes.services;

import com.gestaoiogurtes.api.ApiQuery;
import com.gestaoiogurtes.api.QueryState;
import com.gestaoiogurtes.api.RetrofitClient;
import com.gestaoiogurtes.api.services.IEncomendaMpApiService;
import com.gestaoiogurtes.models.PaginatedResponse;
import com.gestaoiogurtes.models.encomendaMp.CreateEncomendaMpRequest;
import com.gestaoiogurtes.models.encomendaMp.EncomendaMpResponse;

import java.util.function.Consumer;

/**
 * Serviço da aplicação para encomendas de matéria prima.
 * Delega todas as chamadas HTTP ao Retrofit via {@link ApiQuery}.
 */
public class EncomendaMpService {

    private IEncomendaMpApiService api() {
        return RetrofitClient.getInstance().getService(IEncomendaMpApiService.class);
    }

    /**
     * GET /encomendas-mp — lista paginada de todas as encomendas MP.
     */
    public void getAll(int page, int size,
                       Consumer<QueryState<PaginatedResponse<EncomendaMpResponse>>> cb) {
        ApiQuery.execute(api().findAll(page, size), cb);
    }

    /**
     * GET /encomendas-mp/estado/{estado} — lista paginada filtrada por estado.
     * @param estado um dos valores: PENDENTE, ENCOMENDADA, RECEBIDA, CANCELADA
     */
    public void getByEstado(String estado, int page, int size,
                            Consumer<QueryState<PaginatedResponse<EncomendaMpResponse>>> cb) {
        ApiQuery.execute(api().findByEstado(estado, page, size), cb);
    }

    /**
     * GET /encomendas-mp/{id} — detalhe completo de uma encomenda MP.
     */
    public void getById(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().findById(id), cb);
    }

    /**
     * POST /encomendas-mp — criar nova encomenda MP.
     */
    public void create(CreateEncomendaMpRequest request,
                       Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().create(request), cb);
    }

    /**
     * PATCH /encomendas-mp/{id}/aprovar — aprovar e encomendar.
     */
    public void aprovar(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().aprovar(id), cb);
    }

    /**
     * PATCH /encomendas-mp/{id}/cancelar — cancelar a encomenda.
     */
    public void cancelar(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().cancelar(id), cb);
    }

    /**
     * PATCH /encomendas-mp/{id}/recebida — marcar como recebida.
     */
    public void marcarRecebida(String id, Consumer<QueryState<EncomendaMpResponse>> cb) {
        ApiQuery.execute(api().marcarRecebida(id), cb);
    }
}
