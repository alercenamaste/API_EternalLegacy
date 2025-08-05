package com.eternal.api.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.logging.Logger;

@Path("/userAWS")
public class PingResource {
    private static final Logger logger = Logger.getLogger(PingResource.class.getName());

    @POST
    @Path("/post_ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response post_ping() {
        logger.info("Ping endpoint reached");
        return Response.ok("{\"message\": \"pong\"}").build();
    }
    
    @GET
    @Path("/prueba_ping")
    public Response get_ping() {
        return Response
                .ok("ping")
                .build();
    }
    
}
