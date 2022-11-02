package org.acme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.entity.Exercise;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class ExerciseDto {
    private Long id;
    private String name;
    private String description;
    private ExerciseLevel level;
    private List<String> muscles;
    private String img;
    private String video;

    public ExerciseDto(Exercise exercise) {
        this.id = exercise.id;
        this.name = exercise.name;
        this.description = exercise.description;
        this.level = exercise.level;
        this.muscles = exercise.muscles;
        this.img = exercise.img;
    }

    public Exercise toEntity() {
        final var exercise = new Exercise();

        exercise.id = this.id;
        exercise.name = this.name;
        exercise.description = this.description;
        exercise.level = this.level;
        exercise.muscles = this.muscles;
        exercise.img = this.img;
        exercise.video = this.video;
        exercise.users = Collections.emptyList();

        return exercise;
    }
}
