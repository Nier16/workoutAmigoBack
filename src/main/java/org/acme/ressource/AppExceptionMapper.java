package org.acme.ressource;

import io.vertx.ext.web.handler.HttpException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<HttpException> {
    @Override
    public Response toResponse(HttpException exception) {
        return Response.status(exception.getStatusCode()).entity(exception.getPayload()).build();
    }
}

