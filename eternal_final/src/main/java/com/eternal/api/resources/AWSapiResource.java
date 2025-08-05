package com.eternal.api.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class AWSapiResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pingPost(String input) {
        System.out.println("Recibido POST en /ping con datos: " + input);
        return Response.ok("{\"message\": \"pong POST\"}").build();
    }
}
