package org.acme.ressource;

import org.acme.entity.User;
import org.acme.model.Role;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes("application/json")
@Produces(MediaType.APPLICATION_JSON)
@Path("/users")
public class UserResource {


    @RolesAllowed({Role.ADMIN_NAME})
    @GET
    public Response users() {
        return Response.ok(User.listAll()).build();
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
}
