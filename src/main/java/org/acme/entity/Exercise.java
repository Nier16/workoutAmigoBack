package org.acme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.acme.converter.ListStringConverter;
import org.acme.model.ExerciseLevel;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "level")
    private ExerciseLevel level;

    @Convert(converter = ListStringConverter.class)
    @Column(name = "muscles")
    private List<String> muscles;

    @Column(name = "img", columnDefinition = "varchar(320000)")
    private String img;
}
