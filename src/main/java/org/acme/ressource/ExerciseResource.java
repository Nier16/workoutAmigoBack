package org.acme.ressource;

import io.vertx.ext.web.handler.HttpException;
import org.acme.entity.Exercise;
import org.acme.model.ExerciseDto;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("exercises")
public class ExerciseResource {

    @GET
    public Response list() {
        return Response.ok(Exercise.listAll().stream().map(ex -> new ExerciseDto((Exercise) ex)).collect(Collectors.toList())).build();
    }

    @GET
    @Path("/{id}")
    public Response exerciseById(Long id) {
        return Response.ok(new ExerciseDto(this.getExercise(id))).build();
    }

    @POST
    @Transactional
    public Response create(ExerciseDto exerciseDto) {
        final Exercise exercise = exerciseDto.toEntity();
        exercise.persist();
        return Response.created(URI.create("/exercises/" + exercise.id)).build();
    }

    public Exercise getExercise(Long id) {
        final Exercise exercise = Exercise.findById(id);
        if(exercise == null) {
            throw new HttpException(Response.Status.NOT_FOUND.getStatusCode(), "This exercise does not exist");
        }
        return exercise;
    }
}
