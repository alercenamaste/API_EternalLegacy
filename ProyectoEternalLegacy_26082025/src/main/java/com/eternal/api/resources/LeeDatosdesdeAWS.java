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
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

/**
 * Clase añadida automáticamente: LeeDatosdesdeAWS Exposición del endpoint
 * /login (POST)
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
                    .entity("{\"error\": \"Username and password are required\"}")
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
                    if (resultSet.next()) {
                        // Usuario encontrado
                        return Response.status(Response.Status.OK)
                                .entity("{\"message\": \"Login successful\"}")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Credentials", "true")
                                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                                .build();
                    } else {
                        // Usuario no encontrado
                        return Response.status(Response.Status.UNAUTHORIZED)
                                .entity("{\"error\": \"Invalid username or password\"}")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                                .build();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("{\"error\": \"Database error occurred\"}")
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
                    .entity("{\"error\": \"Database error occurred\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();
        }
    }

    @POST
    @Path("/insertUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserPostgres user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getUsername()); // Cambié de getNombre() a getUsername()
                statement.setString(2, user.getEmail());
                statement.executeUpdate();
            }
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"User created successfully\"}")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to create user\"}")
                    .build();
        }
    }

    @POST
    @Path("/insertRegister")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUserRegister(InsertUserRegister user) {
        // Validación de datos recibidos
        if (user.getUsername() == null || user.getUsername().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()
                || user.getName() == null || user.getName().isEmpty()
                || user.getEmail() == null || user.getEmail().isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"All fields (username, password, name, email) are required\"}")
                    .build();
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO usersregister (username, password, name, email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getName());
                statement.setString(4, user.getEmail());

                int rowsInserted = statement.executeUpdate();
                System.out.println("insertandoRegister:" + rowsInserted);

                if (rowsInserted > 0) {
                    return Response.status(Response.Status.CREATED)
                            .entity("{\"message\": \"User created successfully\"}")
                            .build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("{\"error\": \"Failed to insert user\"}")
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generarPresignedUrl(ArchivoRequest request) {

        try (S3Presigner presigner = S3Presigner.builder()
                .region(REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            //String key = "uploads/" + request.fileName(); // puedes generar carpetas dinámicas aquí
            // Definimos el nombre del archivo en S3 usando la carpeta correcta
            String key = "contenedoreternallegacyqrinicio/" + request.getFileName();
            System.out.println("Servicio Upload Key :MO");

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .contentType(request.getFileType())
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r
                    .putObjectRequest(objectRequest)
                    .signatureDuration(Duration.ofMinutes(10)) // válido por 10 min
            );

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("url", presignedRequest.url().toString());
            responseMap.put("key", key);

            return Response.ok(responseMap).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al generar la URL: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/insertRegisterContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUserRegister(InsertContenidoUsuario user) {
        // Validación de datos requeridos
        if (user.getUrl() == null || user.getUrl().isEmpty()
                || user.getEmail() == null || user.getEmail().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"All fields (URL, email) are required.\"}")
                    .build();
        }

        // Conexión y ejecución del INSERT
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO usersregisterContenido (URL, email) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getUrl()); // CORREGIDO: Llamada correcta a getter
                statement.setString(2, user.getEmail());

                int rowsInserted = statement.executeUpdate();
                System.out.println("insertandoRegister: " + rowsInserted);

                if (rowsInserted > 0) {
                    return Response.status(Response.Status.CREATED)
                            .entity("{\"message\": \"Contenido user created successfully.\"}")
                            .build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("{\"error\": \"Failed to insert contenido user.\"}")
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/UrlsByEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUrlsByEmail(RequestLeeDtos request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email is required\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();
        }

        List<String> urls = new ArrayList<>();
        String sql = "SELECT URL FROM usersregisterContenido WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    urls.add(resultSet.getString("URL"));
                }
            }

            if (urls.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"No URLs found for the given email\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Credentials", "true")
                        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                        .build();
            }

            String jsonResponse = "{\"urls\": [\"" + String.join("\",\"", urls) + "\"]}";

            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Database error: " + e.getMessage() + "\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();
        }

    }

}//principal
