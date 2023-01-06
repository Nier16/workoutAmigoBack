package org.acme.controller;

import lombok.AllArgsConstructor;
import org.acme.entity.Exercise;
import org.acme.exception.HttpException;
import org.acme.model.ExerciseDto;
import org.acme.repository.ExerciseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exercises")
@AllArgsConstructor
public class ExerciseController {


    private final ExerciseRepository exerciseRepository;

    @GetMapping
    public ResponseEntity<List<ExerciseDto>> list() {
        return ResponseEntity.ok(exerciseRepository.findAll().stream().map(ExerciseDto::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDto> exerciseById(@PathVariable Long id) throws HttpException {
        return ResponseEntity.ok(new ExerciseDto(this.getExercise(id)));
    }

    @PostMapping
    public ResponseEntity<Exercise> create(ExerciseDto exerciseDto) {
        final Exercise entity = this.exerciseRepository.save(exerciseDto.toEntity());
        return ResponseEntity.created(URI.create("/exercises/" + entity.getId())).build();
    }

    public Exercise getExercise(Long id) throws HttpException {
        final Optional<Exercise> exercise = this.exerciseRepository.findById(id);
        if(exercise.isEmpty()) {
            throw new HttpException(HttpStatus.NOT_FOUND, "This exercise does not exist");
        }
        return exercise.get();
    }
}
