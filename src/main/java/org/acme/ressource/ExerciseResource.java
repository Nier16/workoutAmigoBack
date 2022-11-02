package org.acme.ressource;

import io.quarkus.security.Authenticated;
import io.vertx.ext.web.handler.HttpException;
import org.acme.entity.Exercise;
import org.acme.model.ExerciseDto;
import org.acme.model.Role;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class ExerciseResource {

    @GET
    @Path("/exercises")
    public Response list() {
        return Response.ok(Exercise.listAll().stream().map(ex -> new ExerciseDto((Exercise) ex)).collect(Collectors.toList())).build();
    }

    @GET
    @Path("/exercises/{id}")
    public Response exerciseById(Long id) {
        return Response.ok(new ExerciseDto(this.getExercise(id))).build();
    }

    @GET
    @Path("/exercises/{id}/video")
    public Response exerciseVideo(Long id) {
        return Response.ok(this.getExercise(id).video).build();
    }

    @RolesAllowed({Role.USER_NAME, Role.ADMIN_NAME})
    @POST
    @Path("/secure/exercises")
    @Transactional
    public Response create(ExerciseDto exerciseDto) {
        final Exercise exercise = exerciseDto.toEntity();
        exercise.persist();
        return Response.created(URI.create("/exercises/" + exercise.id)).build();
    }

    @Authenticated
    @PUT
    @Path("/secure/exercises")
    @Transactional
    public Response update(ExerciseDto exerciseDto) {
        if(Exercise.findById(exerciseDto.getId()) == null) {
            throw new NotFoundException();
        }

        exerciseDto.toEntity().persist();

        return Response.created(URI.create("/exercises/" + exerciseDto.getId())).build();
    }

    @Authenticated
    @DELETE
    @Path("/secure/exercises/{id}")
    @Transactional
    public void delete(Long id) {
        Exercise entity = Exercise.findById(id);
        if(entity == null) {
            throw new NotFoundException();
        }

        entity.delete();
    }

    public Exercise getExercise(Long id) {
        final Exercise exercise = Exercise.findById(id);
        if(exercise == null) {
            throw new HttpException(Response.Status.NOT_FOUND.getStatusCode(), "This exercise does not exist");
        }
        return exercise;
    }
}
