package org.example.medmanagement.dto;

import java.util.Set;

/**
 * DTO używane przy rejestracji nowego użytkownika.
 */
public class RegisterRequest {
    private String email;
    private String password;
    private Set<String> roles;

    public RegisterRequest() { }

    public RegisterRequest(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
