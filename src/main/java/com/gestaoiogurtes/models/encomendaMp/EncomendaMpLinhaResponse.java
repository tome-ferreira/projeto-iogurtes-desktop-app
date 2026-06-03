package com.gestaoiogurtes.models.encomendaMp;

import java.util.UUID;

/**
 * DTO de resposta para as linhas de uma encomenda MP.
 * Campos == chaves JSON exactas (EncomendaMPLinhaResponse da API).
 */
public class EncomendaMpLinhaResponse {
    public UUID   id;
    public UUID   materiaId;
    public String materiaNome;
    public String materiaUnidade;
    public Double quantidade;
    public Double precoUnitario;
    public Double precoUnitarioEur;
    public Double taxaIva;
    public Double subtotal;
    public Double subtotalEur;
    public String createdAt;

    public EncomendaMpLinhaResponse() {}
}
