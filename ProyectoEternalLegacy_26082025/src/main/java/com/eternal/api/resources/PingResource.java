package com.eternal.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource {
    @GET
    @Produces("application/json")
    public Response ping() {
        return Response.ok("{\"status\": \"ok\"}").build();
    }
}
