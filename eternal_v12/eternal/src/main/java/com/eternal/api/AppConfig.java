package com.eternal.api;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("com.eternal.api.resources");
    }
}
