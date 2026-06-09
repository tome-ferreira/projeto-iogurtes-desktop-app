package com.gestaoiogurtes.utils;

import java.util.UUID;

/**
 * Serviço singleton para acesso global a dados da sessão do utilizador.
 * Utilização: SessionManager.getInstance().getUserId()
 */
public class SessionManager {

    private static SessionManager instance;

    private UUID userId  = UUID.fromString("92826f54-5f8d-4e27-9362-2b3e555c18b2");
    private String userRole  = "ADMIN";
    private String userName  = "António Silva";
    private String userEmail = "antonio.silva@empresa.com";

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    // Getters
    public UUID   getUserId()    { return userId; }
    public String getUserRole()  { return userRole; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }

    // Setters
    public void setUserId(UUID userId)       { this.userId = userId; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserEmail(String email)   { this.userEmail = email; }
}

/*
// Ler
UUID id      = SessionManager.getInstance().getUserId();
String role  = SessionManager.getInstance().getUserRole();
String name  = SessionManager.getInstance().getUserName();
String email = SessionManager.getInstance().getUserEmail();

// Escrever
SessionManager.getInstance().setUserRole("GESTOR");*/