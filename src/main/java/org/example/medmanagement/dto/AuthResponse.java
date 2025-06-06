package org.example.medmanagement.dto;

public class AuthResponse {
    private String token;

    public AuthResponse() { }

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter i setter dla token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
