package org.acme.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthResponse {
    public String token;
    public Set<Role> roles;
}

