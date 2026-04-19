package com.gestaoiogurtes.api.iogurtes;

import com.gestaoiogurtes.model.IogurteVM;

import java.util.List;

/**
 * Real HTTP/REST implementation of {@link IIogurtesApiService}.
 *
 * This is an empty stub. Implement each method with actual HTTP calls
 * when the backend API is ready.
 *
 * To activate, set {@code USE_MOCK = false} in {@link IogurtesApiServiceFactory}.
 */
public class RealIogurtesApiService implements IIogurtesApiService {

    @Override
    public List<IogurteVM> listarTodos() {
        // TODO: GET /api/iogurtes
        throw new UnsupportedOperationException("Real API not implemented yet");
    }

    @Override
    public void adicionar(IogurteVM iogurte) {
        // TODO: POST /api/iogurtes
        throw new UnsupportedOperationException("Real API not implemented yet");
    }

    @Override
    public void atualizar(IogurteVM original, IogurteVM editado) {
        // TODO: PUT /api/iogurtes/{id}
        throw new UnsupportedOperationException("Real API not implemented yet");
    }

    @Override
    public void remover(IogurteVM iogurte) {
        // TODO: DELETE /api/iogurtes/{id}
        throw new UnsupportedOperationException("Real API not implemented yet");
    }
}
