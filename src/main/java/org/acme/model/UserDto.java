package org.acme.model;

import lombok.Data;
import org.acme.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDto {
    private final String username;

    private final Set<Role> roles;
    private final Set<Long> favorites;

    public UserDto(final User user) {
        this.username = user.username;
        this.favorites = user.favorites.stream().map(f -> f.id).collect(Collectors.toSet());
        this.roles = user.roles;
    }
}
