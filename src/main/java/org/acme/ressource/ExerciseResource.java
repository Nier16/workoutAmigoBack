package org.acme.ressource;

import io.quarkus.security.Authenticated;
import io.vertx.ext.web.handler.HttpException;
import org.acme.entity.Exercise;
import org.acme.model.ExerciseDto;
import org.acme.model.Role;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

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

    @Operation(summary = "Get the list of all exercises (without video)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Exercises returned",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ExerciseDto[].class))})
    })
    @GET
    @Path("/exercises")
    public Response list() {
        return Response.ok(Exercise.listAll().stream().map(ex -> new ExerciseDto((Exercise) ex)).collect(Collectors.toList())).build();
    }

    @Operation(summary = "Get the exercise with the id passed as PathParam (without video)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Exercise returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseDto.class)) }),
            @APIResponse(responseCode = "404", description = "Exercise not found in the data base",
                    content = @Content) })
    @GET
    @Path("/exercises/{id}")
    public Response exerciseById(Long id) {
        return Response.ok(new ExerciseDto(this.getExercise(id))).build();
    }


    @Operation(summary = "Get the video of the exercise with the id passed as PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Video returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseDto.class)) }),
            @APIResponse(responseCode = "404", description = "Exercise not found in the data base",
                    content = @Content) })
    @GET
    @Path("/exercises/{id}/video")
    public Response exerciseVideo(Long id) {
        return Response.ok(this.getExercise(id).video).build();
    }


    @Operation(summary = "Create new exercise (secured)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Exercise created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseDto.class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content) })
    @RolesAllowed({Role.USER_NAME, Role.ADMIN_NAME})
    @POST
    @Path("/secure/exercises")
    @Transactional
    public Response create(ExerciseDto exerciseDto) {
        final Exercise exercise = exerciseDto.toEntity();
        exercise.persist();
        return Response.created(URI.create("/exercises/" + exercise.id)).build();
    }

    @Operation(summary = "Update exercise, the id passed in the Exercise will be used to change the existing one (secured)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Exercise updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseDto.class)) }),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "404", description = "Exercise not found in the data base",
                    content = @Content) })
    @Authenticated
    @PUT
    @Path("/secure/exercises")
    @Transactional
    public Response update(ExerciseDto exerciseDto) {
        // THREW EXCEPTION IF NOT FOUND
        this.getExercise(exerciseDto.getId());

        exerciseDto.toEntity().persist();

        return Response.created(URI.create("/exercises/" + exerciseDto.getId())).build();
    }

    @Operation(summary = "Delete the exercise with the id passed as PathParam (admin)")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Exercise deleted"),
            @APIResponse(responseCode = "401", description = "Token passed in the header is not valid or expired",
                    content = @Content),
            @APIResponse(responseCode = "404", description = "Exercise not found in the data base",
                    content = @Content) })
    @Authenticated
    @DELETE
    @Path("/secure/exercises/{id}")
    @Transactional
    public void delete(Long id) {
        Exercise entity = this.getExercise(id);
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
