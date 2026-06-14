package com.gestaoiogurtes.utils;

import java.util.UUID;

public class SessionManager {

    private static SessionManager instance;

    private UUID   userId;
    private String userRole;
    private String userName;
    private String userEmail;
    private String authToken;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public UUID   getUserId()    { return userId; }
    public String getUserRole()  { return userRole; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getAuthToken() { return authToken; }


    public void setUserId(UUID userId)       { this.userId    = userId; }
    public void setUserRole(String userRole) { this.userRole  = userRole; }
    public void setUserName(String userName) { this.userName  = userName; }
    public void setUserEmail(String email)   { this.userEmail = email; }


    public void setAuthToken(String token)   { this.authToken = token; }

    
    public void clearSession() {
        this.userId    = null;
        this.userName  = null;
        this.userRole  = null;
        this.userEmail = null;
        this.authToken = null;
    }
}