package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.entity.Exercise;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExerciseRepository implements PanacheRepository<Exercise> {
}
