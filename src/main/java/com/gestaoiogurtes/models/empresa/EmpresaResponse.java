package com.gestaoiogurtes.models.empresa;

import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
import java.util.UUID;

public class EmpresaResponse {

    public UUID id;
    public String nomeEmpresa;
    public String nipc;
    public String telefone;
    public String morada;
    public String codigoPostal;
    public String cidade;
    public LocalDateTime createdAt;

    public EmpresaResponse() {
    }

    @Override
    public String toString() {
        return "EmpresaResponse{id=" + id + ", nomeEmpresa='" + nomeEmpresa + "'}";
    }
}
