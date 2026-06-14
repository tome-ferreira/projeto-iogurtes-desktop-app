package com.gestaoiogurtes.models.encomendaMp;

import java.util.List;
import java.util.UUID;

public class CreateEncomendaMpRequest {
    public UUID userId;
    public UUID fornecedorId;
    public String observacoes;
    public List<EncomendaMpLinhaItem> linhas;

    public CreateEncomendaMpRequest() {
    }
}
