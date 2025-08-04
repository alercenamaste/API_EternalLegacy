package com.eternal.api.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.logging.Logger;

@Path("/ping")
public class PingResource {
    private static final Logger logger = Logger.getLogger(PingResource.class.getName());

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        logger.info("Ping endpoint reached");
        return Response.ok("{\"message\": \"pong\"}").build();
    }
}
