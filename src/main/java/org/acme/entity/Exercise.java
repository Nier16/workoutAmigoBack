package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.acme.model.ExerciseLevel;

import javax.persistence.*;

@Entity
public class Exercise extends PanacheEntity {
    public String name;
    public String description;
    public String muscles;
    public String img;
    public ExerciseLevel level;
}
