package com.gestaoiogurtes.model;

import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Modelo que representa a resposta JSON do endpoint Empresa.
 *
 * <p>
 * Os nomes dos campos são idênticos às chaves JSON devolvidas pela API
 * para compatibilidade automática com o {@code GsonConverterFactory}.
 * </p>
 *
 * <h3>Campos JSON (EmpresaResponse):</h3>
 * 
 * <pre>
 * {
 *   "id":           "uuid",
 *   "nomeEmpresa":  "string",
 *   "nipc":         "string",
 *   "telefone":     "string",
 *   "morada":       "string",
 *   "codigoPostal": "string",
 *   "cidade":       "string",
 *   "createdAt":    "date-time"
 * }
 * </pre>
 */
public class EmpresaResponse {

    public UUID id;
    public String nomeEmpresa;
    public String nipc;
    public String telefone;
    public String morada;
    public String codigoPostal;
    public String cidade;
    public LocalDateTime createdAt;

    /** Construtor sem argumentos exigido pelo Gson. */
    public EmpresaResponse() {
    }

    @Override
    public String toString() {
        return "EmpresaResponse{id=" + id + ", nomeEmpresa='" + nomeEmpresa + "'}";
    }
}
