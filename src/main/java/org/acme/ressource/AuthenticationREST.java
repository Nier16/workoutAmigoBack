package org.acme.ressource;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.acme.entity.User;
import org.acme.model.AuthRequest;
import org.acme.model.AuthResponse;
import org.acme.model.Role;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import security.PBKDF2Encoder;
import security.TokenUtils;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Consumes("application/json")
@Produces(MediaType.APPLICATION_JSON)
@Path("/auth")
public class AuthenticationREST {

    @Inject
    PBKDF2Encoder passwordEncoder;

    @Inject
    SecurityIdentity securityIdentity;

    @ConfigProperty(name = "com.ard333.quarkusjwt.jwt.duration") public Long duration;
    @ConfigProperty(name = "mp.jwt.verify.issuer") public String issuer;

    @Operation(summary = "Login to the app using username/password ")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Logged successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)) }),
            @APIResponse(responseCode = "401", description = "Wrong credentials",
                    content = @Content) })
    @PermitAll
    @POST
    @Path("/login")
    public Response login(@RequestBody AuthRequest authRequest) {
        Optional<User> u = User.findByUsername(authRequest.username);
        if (u.isPresent() && u.get().password.equals(passwordEncoder.encode(authRequest.password))) {
            try {
                return Response.ok(new AuthResponse(TokenUtils.generateToken(u.get().username, u.get().roles, duration, issuer), u.get().roles)).build();
            } catch (Exception e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Signup to the app using username/password ")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Account created successfully"),
            @APIResponse(responseCode = "400", description = "Both username and password should not be empty",
                    content = @Content),
            @APIResponse(responseCode = "409", description = "A user already exist with this username",
                    content = @Content)})
    @PermitAll
    @POST
    @Path("/signup")
    @Transactional
    public Response signup(AuthRequest authRequest) {

        if(authRequest.password == null || authRequest.password.isBlank() || authRequest.username == null || authRequest.username.isBlank()){
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Both username and password should not be empty").build();
        }

        if(User.findByUsername(authRequest.username).isPresent()){
            return Response.status(Response.Status.CONFLICT.getStatusCode(), "A user already exist with this username").build();
        }

        final Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        if(User.listAll().isEmpty()) {
            roles.add(Role.ADMIN);
        }

        User.persist(new User(authRequest.username, passwordEncoder.encode(authRequest.password), roles, Collections.emptyList()));

        return Response
                .status(Response.Status.CREATED.getStatusCode(), "User correctly created, please try to login with your new credentials")
                .build();
    }


    @Operation(summary = "Validate a token ")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Token validated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))}),
            @APIResponse(responseCode = "401", description = "Token not valide",
                    content = @Content)})
    @Authenticated
    @POST
    @Path("/secure/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        final String token = TokenUtils.getTokenFromAuth(authHeader);

        return Response
                .ok(new AuthResponse(token, this.securityIdentity.getRoles().stream().map(Role::valueOf).collect(Collectors.toSet())))
                .build();
    }
}
