package com.syntaze.backend.domain.model;

import com.syntaze.backend.domain.enums.Role;

import java.util.Collection;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private Role role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role roles) {
        this.role = roles;
    }
}
