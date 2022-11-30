package org.acme.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.*;

@Converter
public class ListStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return Optional.ofNullable(attribute)
                .map(a -> String.join(",", attribute))
                .orElse("");
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
                .filter(data -> !dbData.isBlank())
                .map(data -> data.split(","))
                .map(Arrays::asList)
                .orElse(new ArrayList<>());
    }
}
