package org.acme.ressource;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.handler.HttpException;
import org.acme.entity.Exercise;
import org.acme.entity.User;
import org.acme.model.Role;
import org.acme.model.UserDto;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Consumes("application/json")
@Produces(MediaType.APPLICATION_JSON)
@Path("/secure/users")
public class UserResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    ExerciseResource exerciseResource;

    @Authenticated
    @GET
    public Response activeUser() {
        return Response.ok(new UserDto(this.getUser())).build();
    }

    @Authenticated
    @POST
    @Path("/favorites")
    @Transactional
    public Response addFavorites(Long id) {
        final User user = this.getUser();
        final Exercise exercise = this.exerciseResource.getExercise(id);

        if(user.favorites.stream().anyMatch(ex -> ex.id.equals(id))){
            return Response.status(Response.Status.CONFLICT.getStatusCode(), "This exercise is already in favorites list of this user").build();
        }

        user.favorites.add(exercise);
        exercise.users.add(user);

        exercise.persist();
        user.persist();

        return Response.ok(user.favorites).build();
    }

    @Authenticated
    @DELETE
    @Path("/favorites")
    @Transactional
    public Response deleteFavorites(Long id) {
        final User user = this.getUser();
        final Exercise exercise = this.exerciseResource.getExercise(id);

        if(user.favorites.stream().noneMatch(ex -> ex.id.equals(id))){
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "This exercise does not exist in favorit list").build();
        }

        user.favorites.remove(exercise);
        exercise.users.remove(user);

        exercise.persist();
        user.persist();

        return Response.ok(user.favorites).build();
    }

    @RolesAllowed({Role.ADMIN_NAME})
    @GET
    @Path("/all")
    public Response users() {
        return Response.ok(User.listAll().stream().map(u -> new UserDto((User) u)).collect(Collectors.toList())).build();
    }

    @RolesAllowed({Role.ADMIN_NAME})
    @DELETE
    @Transactional
    public Response deleteAll() {
        return Response.ok(String.format("%s users deleted", User.deleteAll())).build();
    }

    @RolesAllowed({Role.ADMIN_NAME})
    @DELETE
    @Path("/{username}")
    @Transactional
    public Response delete(String username) {
        if(username == null || username.isBlank()){
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "username should be specified").build();
        }

        if(!User.deleteById(username)){
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "username not found").build();
        }
        return Response.ok(String.format("%s user deleted", username)).build();
    }

    private User getUser() {
        var res = User.findByUsername(securityIdentity.getPrincipal().getName());
        if(res.isEmpty()){
            throw new HttpException(Response.Status.NOT_FOUND.getStatusCode(), "User not found in the database");
        }
        return res.get();
    }
}
