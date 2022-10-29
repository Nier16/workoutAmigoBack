package org.acme.controller;

import org.acme.entity.Exercise;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExerciseController {

    @GET
    public List<Exercise> list() {
        return Exercise.listAll();
    }

    @GET
    @Path("/{id}")
    public Exercise exerciseById(Long id) {
        return Exercise.findById(id);
    }

    @POST
    @Transactional
    public Response create(Exercise exercise) {
        exercise.persist();
        return Response.created(URI.create("/exercises/" + exercise.id)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Exercise update(Long id, Exercise exercise) {
        Exercise entity = Exercise.findById(id);
        if(entity == null) {
            throw new NotFoundException();
        }

        entity.name = exercise.name;
        entity.description = exercise.description;
        entity.img = exercise.img;
        entity.level = exercise.level;
        entity.muscles = exercise.muscles;

        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(Long id) {
        Exercise entity = Exercise.findById(id);
        if(entity == null) {
            throw new NotFoundException();
        }

        entity.delete();
    }
}
