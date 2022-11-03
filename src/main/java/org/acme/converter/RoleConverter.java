package org.acme.converter;

import org.acme.model.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.*;
import java.util.stream.Collectors;

@Converter
public class RoleConverter implements AttributeConverter<Set<Role>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Role> attribute) {
        return Optional.ofNullable(attribute)
                .map(a -> attribute.stream().map(Role::name).collect(Collectors.joining(",")))
                .orElse("");
    }

    @Override
    public Set<Role> convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
                .filter(data -> !dbData.isBlank())
                .map(data -> data.split(","))
                .map(dataList -> Arrays.stream(dataList).map(Role::valueOf).collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }
}
