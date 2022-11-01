package org.acme.ressource;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.acme.entity.Exercise;
import org.acme.model.Role;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/exercises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExerciseResource {

    @GET
    public List<Exercise> list() {
        return Exercise.listAll();
    }

    @Authenticated
    @GET
    @Path("/{id}")
    public Exercise exerciseById(Long id) {
        return Exercise.findById(id);
    }

    @RolesAllowed({Role.USER_NAME, Role.ADMIN_NAME})
    @POST
    @Transactional
    public Response create(Exercise exercise) {
        exercise.persist();
        return Response.created(URI.create("/exercises/" + exercise.id)).build();
    }

    @Authenticated
    @PUT
    @Path("/{id}")
    @Transactional
    public Exercise update(Exercise exercise) {
        Exercise entity = Exercise.findById(exercise.id);
        if(entity == null) {
            throw new NotFoundException();
        }

        exercise.persist();

        return exercise;
    }

    @Authenticated
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
