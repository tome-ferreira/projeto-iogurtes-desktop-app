package com.gestaoiogurtes.models.utilizador;

import java.util.UUID;

public class UserResponse {
    public UUID id;
    public String nome;
    public String email;
    public String role;
    public String turno;
    public String empresaId;
    public String dataAdmissao;
    public String createdAt;

    public UserResponse() {
    }
}
