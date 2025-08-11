package com.eternal.api.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import software.amazon.awssdk.regions.Region;

/**
 * Clase añadida automáticamente: LeeDatosdesdeAWS
 * Exposición del endpoint /login (POST)
 */
@Path("/")
public class LeeDatosdesdeAWS {

    private static final String DB_URL = "jdbc:postgresql://database-eternallegacy.c102scy2onhh.us-east-2.rds.amazonaws.com:5432/DBEternalLegacy";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "01Febrero1981";
    private static final String BUCKET_NAME = "contenedoreternallegacyqrinicio";
    private static final Region REGION = Region.US_EAST_2; // Región de Ohio

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(RequestLogin user) {
        // Validación de entrada
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{"error": "Username and password are required"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM usersregister WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if ( resultSet.next()) {
                        // Usuario encontrado
                        return Response.status(Response.Status.OK)
                                .entity("{"message": "Login successful"}")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Credentials", "true")
                                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                                .build();
                    } else {
                        // Usuario no encontrado
                        return Response.status(Response.Status.UNAUTHORIZED)
                                .entity("{"error": "Invalid username or password"}")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                                .build();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("{"error": "Database error occurred"}")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Credentials", "true")
                            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{"error": "Database error occurred"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();

        }
    }
}
