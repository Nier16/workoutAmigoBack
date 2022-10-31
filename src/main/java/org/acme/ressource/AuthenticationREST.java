package org.acme.ressource;

import org.acme.entity.User;
import org.acme.model.AuthRequest;
import org.acme.model.AuthResponse;
import org.acme.model.Role;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import security.PBKDF2Encoder;
import security.TokenUtils;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.Optional;

@Consumes("application/json")
@Produces(MediaType.APPLICATION_JSON)
@Path("/user")
public class AuthenticationREST {

    @Inject
    PBKDF2Encoder passwordEncoder;

    @ConfigProperty(name = "com.ard333.quarkusjwt.jwt.duration") public Long duration;
    @ConfigProperty(name = "mp.jwt.verify.issuer") public String issuer;

    @PermitAll
    @POST
    @Path("/login")
    public Response login(AuthRequest authRequest) {
        Optional<User> u = User.findByUsername(authRequest.username);
        if (u.isPresent() && u.get().password.equals(passwordEncoder.encode(authRequest.password))) {
            try {
                return Response.ok(new AuthResponse(TokenUtils.generateToken(u.get().username, u.get().roles, duration, issuer))).build();
            } catch (Exception e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @PermitAll
    @POST
    @Path("/sign-in")
    @Transactional
    public Response signIn(AuthRequest authRequest) {

        if(authRequest.password == null || authRequest.password.isBlank() || authRequest.username == null || authRequest.username.isBlank()){
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Both username and password should not be empty").build();
        }

        if(User.findByUsername(authRequest.username).isPresent()){
            return Response.status(Response.Status.CONFLICT.getStatusCode(), "A user already exist with this username").build();
        }

        User.persist(new User(authRequest.username, passwordEncoder.encode(authRequest.password), Collections.singleton(Role.USER)));

        return Response
                .status(Response.Status.CREATED.getStatusCode(), "User correctly created, please try to login with your new credentials")
                .build();
    }
}
