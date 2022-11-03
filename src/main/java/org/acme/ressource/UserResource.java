package org.acme.ressource;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.handler.HttpException;
import org.acme.entity.Exercise;
import org.acme.entity.User;
import org.acme.model.Role;
import org.acme.model.UserDto;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

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

    @Operation(summary = "Get the active user for the auth token provided in the header (secured)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Found the user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "404", description = "User not found in the data base",
                    content = @Content) })
    @Authenticated
    @GET
    public Response activeUser() {
        return Response.ok(new UserDto(this.getUser())).build();
    }

    @Operation(summary = "Add the exercise with the id passed in PathParam as favorite for the active user (secured)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Favorite added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long[].class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "404", description = "User Or Exercise not found in the data base",
                    content = @Content),
            @APIResponse(responseCode = "409", description = "This exercise is already in favorites list of this user",
                    content = @Content) })
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

        return Response.ok(user.favorites.stream().map(f -> f.id).collect(Collectors.toList())).build();
    }

    @Operation(summary = "Remove the exercise with the id passed in PathParam from the favorites for the active user (secured)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Favorite removed",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long[].class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "404", description = "User Or Exercise not found in the data base",
                    content = @Content),
            @APIResponse(responseCode = "409", description = "This exercise is not in the favorites of the user",
                    content = @Content) })
    @Authenticated
    @DELETE
    @Path("/favorites")
    @Transactional
    public Response deleteFavorites(Long id) {
        final User user = this.getUser();
        final Exercise exercise = this.exerciseResource.getExercise(id);

        if(user.favorites.stream().noneMatch(ex -> ex.id.equals(id))){
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "This exercise does not exist in favorite list").build();
        }

        user.favorites.remove(exercise);
        exercise.users.remove(user);

        exercise.persist();
        user.persist();

        return Response.ok(user.favorites.stream().map(f -> f.id).collect(Collectors.toList())).build();
    }

    @Operation(summary = "Get all users of the app (admin)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Users returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto[].class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "403", description = "Only admin can have access to this resource",
                    content = @Content) })
    @RolesAllowed({Role.ADMIN_NAME})
    @GET
    @Path("/all")
    public Response users() {
        return Response.ok(User.listAll().stream().map(u -> new UserDto((User) u)).collect(Collectors.toList())).build();
    }

    @Operation(summary = "Delete all users of the app (admin)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Users deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "403", description = "Only admin can have access to this resource",
                    content = @Content) })
    @RolesAllowed({Role.ADMIN_NAME})
    @DELETE
    @Transactional
    public Response deleteAll() {
        return Response.ok(String.format("%s users deleted", User.deleteAll())).build();
    }

    @Operation(summary = "Delete a user of the app by his username as PathParam (admin)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "User deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "403", description = "Only admin can have access to this resource",
                    content = @Content) })
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
