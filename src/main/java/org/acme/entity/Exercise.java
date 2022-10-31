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
    public ExerciseLevel level;

    @Convert(converter = ListStringConverter.class)
    public List<String> muscles;

    @Column(columnDefinition = "varchar(320000)")
    public String img;

    @Column(columnDefinition = "varchar(31457280)")
    public String video;
}
