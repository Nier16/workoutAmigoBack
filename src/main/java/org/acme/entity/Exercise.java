package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.acme.converter.ListStringConverter;
import org.acme.model.ExerciseLevel;

import javax.persistence.*;
import java.util.List;

@Entity
public class Exercise extends PanacheEntity {

    public String name;
    public String description;
    @Convert(converter = ListStringConverter.class)
    public List<String> muscles;
    public String img;

    public String video;
    public ExerciseLevel level;
}
