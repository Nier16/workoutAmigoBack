package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.acme.converter.RoleConverter;
import org.acme.model.Role;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "app_user")
public class User extends PanacheEntity {

    public String username;
    public String password;
    @Convert(converter = RoleConverter.class)
    public Set<Role> roles;

    public static Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }
}
