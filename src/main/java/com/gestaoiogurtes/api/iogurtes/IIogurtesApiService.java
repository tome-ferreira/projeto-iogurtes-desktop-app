package com.gestaoiogurtes.api.iogurtes;

import com.gestaoiogurtes.model.IogurteVM;

import java.util.List;

/**
 * Contract for all Iogurtes data operations.
 *
 * To switch between mock and real implementation, change the single line
 * in IogurtesApiServiceFactory:
 *
 *   static final boolean USE_MOCK = true;  // ← change to false for real API
 */
public interface IIogurtesApiService {

    /** Returns all iogurtes. */
    List<IogurteVM> listarTodos();

    /** Persists a new iogurte. */
    void adicionar(IogurteVM iogurte);

    /**
     * Replaces {@code original} with {@code editado}, preserving id and criadoEm.
     */
    void atualizar(IogurteVM original, IogurteVM editado);

    /** Removes the given iogurte. */
    void remover(IogurteVM iogurte);
}
