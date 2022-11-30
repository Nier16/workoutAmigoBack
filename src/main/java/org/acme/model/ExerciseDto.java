package org.acme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.entity.Exercise;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private Long id;
    private String name;
    private String description;
    private ExerciseLevel level;
    private List<String> muscles;
    private String img;

    public ExerciseDto(Exercise exercise) {
        this(exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getLevel(),
                exercise.getMuscles(),
                exercise.getImg());
    }

    public Exercise toEntity() {
        return new Exercise(this.id,
                this.name,
                this.description,
                this.level,
                this.muscles,
                this.img);
    }
}
