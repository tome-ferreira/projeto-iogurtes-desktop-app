package com.gestaoiogurtes.models.certificacao;

import java.time.LocalDateTime;
import java.util.UUID;

public class CertificacaoResponse {
    public UUID id;
    public String nome;
    public String descricao;
    public Boolean isActive;
    public LocalDateTime createdAt;

    public CertificacaoResponse() {}
}
