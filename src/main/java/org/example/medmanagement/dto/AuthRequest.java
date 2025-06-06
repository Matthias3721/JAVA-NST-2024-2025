package org.example.medmanagement.dto;

/**
 * DTO służące do przesyłania danych logowania (e-mail + hasło).
 */
public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest() { }

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter i setter dla pola email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter i setter dla pola password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
