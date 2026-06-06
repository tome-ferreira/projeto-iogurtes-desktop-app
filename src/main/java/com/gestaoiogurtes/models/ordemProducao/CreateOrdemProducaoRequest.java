package com.gestaoiogurtes.models.ordemProducao;

import java.util.List;
import java.util.UUID;

public class CreateOrdemProducaoRequest {
    public UUID userId;
    public String observacoes;
    public List<OrdemProducaoLinhaItem> produtos;

    public CreateOrdemProducaoRequest() {}
}
