package com.gestaoiogurtes.models.moeda;

import java.util.UUID;

public class MoedaResponse {
    public UUID id;
    public String codigo;
    public String nome;
    public String simbolo;
    public Double taxaConversaoEur;
    public Boolean isActive;
    public String createdAt;
    public String updatedAt;

    public MoedaResponse() {}
}
