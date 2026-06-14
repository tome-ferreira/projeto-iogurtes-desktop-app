package com.gestaoiogurtes.models.auth;

import java.util.UUID;

public class LoginResponse {

    public UUID id;

    public String nome;

    public String email;

    public String role;

    public String token;

    public LoginResponse() {
    }
}
