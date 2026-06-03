package com.gestaoiogurtes.models.encomendaMp;

import java.util.List;
import java.util.UUID;

/**
 * Corpo do POST /encomendas-mp.
 * Campos == chaves JSON exactas (CreateEncomendaMPRequest da API).
 *
 * <ul>
 *   <li>{@code userId} — obrigatório; preenchido automaticamente via {@code SessionManager}</li>
 *   <li>{@code fornecedorId} — obrigatório; seleccionado pelo utilizador</li>
 *   <li>{@code observacoes} — opcional; máximo 200 caracteres</li>
 *   <li>{@code linhas} — obrigatório; mínimo 1 item</li>
 * </ul>
 */
public class CreateEncomendaMpRequest {
    public UUID userId;
    public UUID fornecedorId;
    public String observacoes;
    public List<EncomendaMpLinhaItem> linhas;

    public CreateEncomendaMpRequest() {}
}
