package com.eternal.api.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.regions.Region;

//
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.message.internal.DataSourceProvider.ByteArrayDataSource;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.imageio.ImageIO;

@Path("/")
public class LeeDatosdesdeAWS {

    //private static final String DB_URL ="jdbc:postgresql://eternallegacyqr.cv84auss2hlm.us-east-2.rds.amazonaws.com:5432/eternallegacyqr?sslmode=require";
    //private static final String DB_URL ="jdbc:postgresql://localhost:5432/eternallegacyqr";
    //private static final String DB_USER = "postgres";
    //private static final String DB_PASSWORD = "01Febrero1981;";
    private static final String BUCKET_NAME = "contenedoreternallegacyqr";
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
    public Response loginUser(RequestLogin user) throws SQLException {
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

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT email FROM usersregister WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String email = resultSet.getString("email");

                        String jsonResponse = String.format(
                                "{\"message\": \"Login successful\", \"email\": \"%s\"}", email
                        );

                        return Response.status(Response.Status.OK)
                                .entity(jsonResponse)
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Credentials", "true")
                                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                                .build();
                    } else {
                        return Response.status(Response.Status.UNAUTHORIZED)
                                .entity("{\"error\": \"Invalid username or password\"}")
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                                .build();
                    }
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

        try (Connection connection = DatabaseConnection.getConnection()) {
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
            String key = "contenedoreternallegacy_usuarios/" + request.getFileName();
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
        try (Connection connection = DatabaseConnection.getConnection()) {
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

        try (Connection connection = DatabaseConnection.getConnection();
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

    @GET
    @Path("/GetUrlsQR")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUrlsByTransaccionget(@QueryParam("transaccion_id") String transaccionid) throws SQLException {

        if (transaccionid == null || transaccionid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"transaccion_id is required\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();
        }

        List<String> urls = new ArrayList<>();
        String sql = "SELECT t3.url FROM PAGOS T1, USERSREGISTER T2, USERSREGISTERCONTENIDO T3 "
                + "WHERE T1.transaction_id = ? AND T2.email = T1.email AND T3.email = T2.email";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transaccionid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    urls.add(resultSet.getString("url"));
                }
                if (urls.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"message\": \"No URLs found for the given transaction_id\"}")
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
                String errorJson = "{\"error\": \"Database error: " + e.getMessage().replace("\"", "\\\"") + "\"}";
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errorJson)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Credentials", "true")
                        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                        .build();
            }
        }
    }

    @POST
    @Path("/UrlsQR")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUrlsByTransaccion(RequestIdpago request) {
        String transaccionid = request.getTransaccion_id();

        if (transaccionid == null || transaccionid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"transaccionid is required\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();
        }

        List<String> urls = new ArrayList<>();
        String sql = "SELECT t3.url FROM  PAGOS T1 , USERSREGISTER T2, usersregistercontenido T3 WHERE transaction_id = ? AND T2.email= T1.email AND T3.email = T2.email";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transaccionid);

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

    @POST
    @Path("/deleteFile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFile(Request_deletefile request) {

        if (request == null || request.getUrl() == null || request.getUrl().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"URL is required\"}")
                    .build();
        }

        String url = request.getUrl();
        String email = request.getEmail();

        try {

            // Obtener el Key desde la URL
            String bucketUrlPrefix = "https://" + BUCKET_NAME + ".s3.us-east-2.amazonaws.com/";

            if (!url.startsWith(bucketUrlPrefix)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Invalid S3 URL\"}")
                        .build();
            }

            String key = url.substring(bucketUrlPrefix.length());

            System.out.println("===== DELETE FILE =====");
            System.out.println("Bucket : " + BUCKET_NAME);
            System.out.println("Key    : " + key);
            System.out.println("URL    : " + url);
            System.out.println("=======================");

            // Cliente S3
            try (S3Client s3 = S3Client.builder()
                    .region(REGION)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build()) {

                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build();

                s3.deleteObject(deleteRequest);
            }

            // Eliminar registro en PostgreSQL
            try (Connection connection = DatabaseConnection.getConnection()) {

                String sql = "DELETE FROM usersregisterContenido WHERE url = ? AND email = ?";

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, url);
                    stmt.setString(2, email);

                    int rows = stmt.executeUpdate();

                    System.out.println("Filas eliminadas BD: " + rows);
                }
            }

            return Response.ok("{\"message\":\"File deleted successfully\"}")
                    .build();

        } catch (S3Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"S3 Error: " + e.awsErrorDetails().errorMessage() + "\"}")
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * **NUEVO FORMATO 10-2026 IA***
     */
    @POST
    @Path("/registerPago")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPago(RequestPago request) {

        Connection connection = null;

        BufferedImage qrImage = null;
        String qrUrl = null;

        boolean committed = false;

        try {

            // ============================================================
            // 1. VALIDAR REQUEST
            // ============================================================
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\":false,\"message\":\"Request vacío\"}")
                        .build();
            }

            if (request.getEmail() == null
                    || request.getEmail().trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\":false,\"message\":\"Email requerido\"}")
                        .build();
            }

            if (request.getTransactionId() == null
                    || request.getTransactionId().trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\":false,\"message\":\"transactionId requerido\"}")
                        .build();
            }

            if (request.getAmount() == null
                    || request.getAmount().trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\":false,\"message\":\"amount requerido\"}")
                        .build();
            }

            if (request.getItems() == null
                    || request.getItems().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\":false,\"message\":\"Debe existir al menos un producto\"}")
                        .build();
            }

            String currency = request.getCurrency();

            if (currency == null || currency.trim().isEmpty()) {
                currency = "USD";
            }

            currency = currency.toUpperCase();

            // ============================================================
            // 2. VALIDAR ITEMS
            // ============================================================

            /*
         * Consolidamos productos repetidos.
         *
         * Ejemplo:
         *
         * productId 4 quantity 1
         * productId 4 quantity 2
         *
         * se transforma en:
         *
         * productId 4 quantity 3
         *
         * Esto evita descontar stock incorrectamente.
             */
            Map<Integer, Integer> quantitiesByProduct = new LinkedHashMap<>();

            for (PaymentItemRequest item : request.getItems()) {

                if (item == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"success\":false,\"message\":\"Item inválido\"}")
                            .build();
                }

                if (item.getProductId() <= 0) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"success\":false,\"message\":\"productId inválido\"}")
                            .build();
                }

                if (item.getQuantity() <= 0) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"success\":false,\"message\":\"quantity debe ser mayor que cero\"}")
                            .build();
                }

                int currentQuantity
                        = quantitiesByProduct.getOrDefault(
                                item.getProductId(), 0);

                quantitiesByProduct.put(
                        item.getProductId(),
                        currentQuantity + item.getQuantity());
            }

            // ============================================================
            // 3. OBTENER CONEXIÓN
            // ============================================================
            connection = DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            // ============================================================
            // 4. IDEMPOTENCIA
            // ============================================================
            String idempotencySQL
                    = "SELECT id, order_id, amount, currency, payment_status "
                    + "FROM payments "
                    + "WHERE transaction_id = ?";

            try (PreparedStatement stmt
                    = connection.prepareStatement(idempotencySQL)) {

                stmt.setString(1, request.getTransactionId());

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        int existingPaymentId
                                = rs.getInt("id");

                        int existingOrderId
                                = rs.getInt("order_id");

                        String existingAmount
                                = rs.getBigDecimal("amount").toPlainString();

                        String existingCurrency
                                = rs.getString("currency");

                        String existingStatus
                                = rs.getString("payment_status");

                        connection.rollback();

                        String responseJson
                                = "{"
                                + "\"success\":true,"
                                + "\"alreadyProcessed\":true,"
                                + "\"paymentId\":" + existingPaymentId + ","
                                + "\"orderId\":" + existingOrderId + ","
                                + "\"transactionId\":\""
                                + escapeJson(request.getTransactionId())
                                + "\","
                                + "\"amount\":" + existingAmount + ","
                                + "\"currency\":\""
                                + escapeJson(existingCurrency)
                                + "\","
                                + "\"paymentStatus\":\""
                                + escapeJson(existingStatus)
                                + "\","
                                + "\"message\":\"La transacción ya fue procesada\""
                                + "}";

                        return Response.ok(responseJson).build();
                    }
                }
            }

            // ============================================================
            // 5. OBTENER PRODUCTOS Y BLOQUEARLOS
            // ============================================================
            String productSQL
                    = "SELECT id, code, sku, price, currency, stock, active "
                    + "FROM catalog_product "
                    + "WHERE id = ? "
                    + "FOR UPDATE";

            String translationSQL
                    = "SELECT product_name "
                    + "FROM catalog_translation "
                    + "WHERE product_id = ? "
                    + "AND language_code = 'en' "
                    + "AND product_name IS NOT NULL "
                    + "LIMIT 1";

            String translationFallbackSQL
                    = "SELECT product_name "
                    + "FROM catalog_translation "
                    + "WHERE product_id = ? "
                    + "AND product_name IS NOT NULL "
                    + "LIMIT 1";

            List<ProductPaymentData> products
                    = new ArrayList<>();

            BigDecimal subtotal
                    = BigDecimal.ZERO;

            try (PreparedStatement productStmt
                    = connection.prepareStatement(productSQL);
                    PreparedStatement translationStmt
                    = connection.prepareStatement(translationSQL);
                    PreparedStatement translationFallbackStmt
                    = connection.prepareStatement(translationFallbackSQL)) {

                for (Map.Entry<Integer, Integer> entry
                        : quantitiesByProduct.entrySet()) {

                    int productId = entry.getKey();
                    int quantity = entry.getValue();

                    // ----------------------------------------------------
                    // Producto
                    // ----------------------------------------------------
                    productStmt.setInt(1, productId);

                    try (ResultSet rs
                            = productStmt.executeQuery()) {

                        if (!rs.next()) {

                            connection.rollback();

                            return Response.status(
                                    Response.Status.BAD_REQUEST)
                                    .entity(
                                            "{\"success\":false,\"message\":\"Producto no existe: "
                                            + productId + "\"}")
                                    .build();
                        }

                        boolean active
                                = rs.getBoolean("active");

                        if (!active) {

                            connection.rollback();

                            return Response.status(
                                    Response.Status.CONFLICT)
                                    .entity(
                                            "{\"success\":false,\"message\":\"Producto inactivo: "
                                            + productId + "\"}")
                                    .build();
                        }

                        int stock
                                = rs.getInt("stock");

                        if (stock < quantity) {

                            connection.rollback();

                            return Response.status(
                                    Response.Status.CONFLICT)
                                    .entity(
                                            "{\"success\":false,\"message\":\"Stock insuficiente para producto "
                                            + productId
                                            + ". Stock disponible: "
                                            + stock
                                            + "\"}")
                                    .build();
                        }

                        BigDecimal price
                                = rs.getBigDecimal("price");

                        String productCode
                                = rs.getString("code");

                        String sku
                                = rs.getString("sku");

                        String productCurrency
                                = rs.getString("currency");

                        if (productCurrency == null
                                || productCurrency.trim().isEmpty()) {

                            productCurrency = "USD";
                        }

                        productCurrency
                                = productCurrency.toUpperCase();

                        if (!productCurrency.equals(currency)) {

                            connection.rollback();

                            return Response.status(
                                    Response.Status.CONFLICT)
                                    .entity(
                                            "{\"success\":false,\"message\":\"La moneda del producto "
                                            + productCode
                                            + " ("
                                            + productCurrency
                                            + ") no coincide con la moneda del pago ("
                                            + currency
                                            + ")\"}")
                                    .build();
                        }

                        // ------------------------------------------------
                        // Nombre del producto
                        // ------------------------------------------------
                        String productName = null;

                        translationStmt.setInt(1, productId);

                        try (ResultSet trs
                                = translationStmt.executeQuery()) {

                            if (trs.next()) {
                                productName
                                        = trs.getString("product_name");
                            }
                        }

                        if (productName == null
                                || productName.trim().isEmpty()) {

                            translationFallbackStmt.setInt(
                                    1, productId);

                            try (ResultSet trs
                                    = translationFallbackStmt
                                            .executeQuery()) {

                                        if (trs.next()) {
                                            productName
                                                    = trs.getString(
                                                            "product_name");
                                        }
                                    }
                        }

                        // Fallback definitivo
                        if (productName == null
                                || productName.trim().isEmpty()) {

                            productName = productCode;
                        }

                        // ------------------------------------------------
                        // Calcular total
                        // ------------------------------------------------
                        BigDecimal itemTotal
                                = price.multiply(
                                        BigDecimal.valueOf(quantity));

                        subtotal
                                = subtotal.add(itemTotal);

                        ProductPaymentData product
                                = new ProductPaymentData();

                        product.setProductId(productId);
                        product.setCode(productCode);
                        product.setSku(sku);
                        product.setProductName(productName);
                        product.setPrice(price);
                        product.setQuantity(quantity);
                        product.setTotal(itemTotal);

                        products.add(product);
                    }
                }
            }

            // ============================================================
            // 6. TOTAL
            // ============================================================
            BigDecimal shippingCost
                    = BigDecimal.ZERO;

            BigDecimal total
                    = subtotal.add(shippingCost);

            BigDecimal receivedAmount;

            try {

                receivedAmount
                        = new BigDecimal(
                                request.getAmount().trim())
                                .setScale(2, RoundingMode.HALF_UP);

            } catch (NumberFormatException e) {

                connection.rollback();

                return Response.status(
                        Response.Status.BAD_REQUEST)
                        .entity(
                                "{\"success\":false,\"message\":\"amount inválido\"}")
                        .build();
            }

            total
                    = total.setScale(
                            2,
                            RoundingMode.HALF_UP);

            // ============================================================
            // 7. COMPARAR MONTO
            // ============================================================
            if (receivedAmount.compareTo(total) != 0) {

                connection.rollback();

                String message
                        = "El monto recibido ("
                        + receivedAmount.toPlainString()
                        + ") no coincide con el total calculado ("
                        + total.toPlainString()
                        + ")";

                return Response.status(
                        Response.Status.CONFLICT)
                        .entity(
                                "{\"success\":false,\"message\":\""
                                + escapeJson(message)
                                + "\"}")
                        .build();
            }

            // ============================================================
            // 8. GENERAR QR
            // ============================================================
            String qrText
                    = "http://ec2-3-141-153-150.us-east-2.compute.amazonaws.com/"
                    + request.getTransactionId();

            qrImage
                    = QRGenerator.generateQR(
                            qrText,
                            400,
                            400);

            qrUrl
                    = S3UploaderQR.uploadQRToS3(
                            qrImage,
                            "pago_"
                            + request.getTransactionId());

            // ============================================================
            // 9. CREAR ORDER NUMBER
            // ============================================================
            String orderNumber;

            int attempts = 0;

            while (true) {

                attempts++;

                orderNumber
                        = generateOrderNumber();

                String checkOrderSQL
                        = "SELECT 1 FROM orders WHERE order_number = ?";

                try (PreparedStatement checkStmt
                        = connection.prepareStatement(
                                checkOrderSQL)) {

                    checkStmt.setString(
                            1,
                            orderNumber);

                    try (ResultSet rs
                            = checkStmt.executeQuery()) {

                        if (!rs.next()) {
                            break;
                        }
                    }
                }

                if (attempts >= 5) {

                    connection.rollback();

                    return Response.status(
                            Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(
                                    "{\"success\":false,\"message\":\"No fue posible generar order_number único\"}")
                            .build();
                }
            }

            // ============================================================
            // 10. INSERT ORDERS
            // ============================================================
            String orderSQL
                    = "INSERT INTO orders ("
                    + "order_number, "
                    + "email, "
                    + "subtotal, "
                    + "shipping_cost, "
                    + "total, "
                    + "currency, "
                    + "status"
                    + ") VALUES (?, ?, ?, ?, ?, ?, 'PAID') "
                    + "RETURNING id";

            int orderId;

            try (PreparedStatement stmt
                    = connection.prepareStatement(orderSQL)) {

                stmt.setString(
                        1,
                        orderNumber);

                stmt.setString(
                        2,
                        request.getEmail());

                stmt.setBigDecimal(
                        3,
                        subtotal);

                stmt.setBigDecimal(
                        4,
                        shippingCost);

                stmt.setBigDecimal(
                        5,
                        total);

                stmt.setString(
                        6,
                        currency);

                try (ResultSet rs
                        = stmt.executeQuery()) {

                    if (!rs.next()) {

                        throw new SQLException(
                                "No se pudo obtener order_id");

                    }

                    orderId
                            = rs.getInt("id");
                }
            }

            // ============================================================
            // 11. INSERT ORDER_ITEMS
            // ============================================================
            String orderItemSQL
                    = "INSERT INTO order_items ("
                    + "order_id, "
                    + "product_id, "
                    + "product_name, "
                    + "sku, "
                    + "quantity, "
                    + "unit_price, "
                    + "total_price, "
                    + "product_image_url, "
                    + "product_format"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt
                    = connection.prepareStatement(
                            orderItemSQL)) {

                for (ProductPaymentData product
                        : products) {

                    stmt.setInt(
                            1,
                            orderId);

                    stmt.setInt(
                            2,
                            product.getProductId());

                    stmt.setString(
                            3,
                            product.getProductName());

                    stmt.setString(
                            4,
                            product.getSku());

                    stmt.setInt(
                            5,
                            product.getQuantity());

                    stmt.setBigDecimal(
                            6,
                            product.getPrice());

                    stmt.setBigDecimal(
                            7,
                            product.getTotal());

                    // Todavía no dependemos de catalog_image.
                    stmt.setNull(
                            8,
                            Types.VARCHAR);

                    // El checkout actual todavía no envía formato.
                    stmt.setNull(
                            9,
                            Types.VARCHAR);

                    stmt.addBatch();
                }

                stmt.executeBatch();
            }

            // ============================================================
            // 12. DESCONTAR STOCK
            // ============================================================
            String updateStockSQL
                    = "UPDATE catalog_product "
                    + "SET stock = stock - ? "
                    + "WHERE id = ? "
                    + "AND stock >= ?";

            try (PreparedStatement stmt
                    = connection.prepareStatement(
                            updateStockSQL)) {

                for (ProductPaymentData product
                        : products) {

                    stmt.setInt(
                            1,
                            product.getQuantity());

                    stmt.setInt(
                            2,
                            product.getProductId());

                    stmt.setInt(
                            3,
                            product.getQuantity());

                    int updated
                            = stmt.executeUpdate();

                    if (updated != 1) {

                        throw new SQLException(
                                "No fue posible descontar stock del producto "
                                + product.getProductId());
                    }
                }
            }

            // ============================================================
            // 13. INSERT PAYMENTS
            // ============================================================
            String paymentSQL
                    = "INSERT INTO payments ("
                    + "order_id, "
                    + "email, "
                    + "transaction_id, "
                    + "amount, "
                    + "currency, "
                    + "payment_method, "
                    + "payment_status, "
                    + "qr_url"
                    + ") VALUES (?, ?, ?, ?, ?, 'PAYPAL', 'PAID', ?) "
                    + "RETURNING id";

            int paymentId;

            try (PreparedStatement stmt
                    = connection.prepareStatement(
                            paymentSQL)) {

                stmt.setInt(
                        1,
                        orderId);

                stmt.setString(
                        2,
                        request.getEmail());

                stmt.setString(
                        3,
                        request.getTransactionId());

                stmt.setBigDecimal(
                        4,
                        total);

                stmt.setString(
                        5,
                        currency);

                stmt.setString(
                        6,
                        qrUrl);

                try (ResultSet rs
                        = stmt.executeQuery()) {

                    if (!rs.next()) {

                        throw new SQLException(
                                "No se pudo obtener payment_id");
                    }

                    paymentId
                            = rs.getInt("id");
                }
            }

            // ============================================================
            // 14. INSERT LEGACY pagos
            // ============================================================
            String legacyPagoSQL
                    = "INSERT INTO pagos ("
                    + "email, "
                    + "transaction_id, "
                    + "amount, "
                    + "url_qr"
                    + ") VALUES (?, ?, ?, ?) "
                    + "RETURNING id";

            int pagoId;

            try (PreparedStatement stmt
                    = connection.prepareStatement(
                            legacyPagoSQL)) {

                stmt.setString(
                        1,
                        request.getEmail());

                stmt.setString(
                        2,
                        request.getTransactionId());

                stmt.setString(
                        3,
                        total.toPlainString());

                stmt.setString(
                        4,
                        qrUrl);

                try (ResultSet rs
                        = stmt.executeQuery()) {

                    if (!rs.next()) {

                        throw new SQLException(
                                "No se pudo obtener pago_id");
                    }

                    pagoId
                            = rs.getInt("id");
                }
            }

            // ============================================================
            // 15. COMMIT
            // ============================================================
            connection.commit();

            committed = true;

            // ============================================================
            // 16. EMAIL DESPUÉS DEL COMMIT
            // ============================================================
            boolean customerEmailSent = false;
            boolean productionEmailSent = false;

            String emailError = null;

            try {
                Map<String, String> customerVariables
                        = buildCustomerEmailVariables(orderId);

                customerEmailSent = sendTemplateEmail(
                        orderId,
                        "PAYMENT_CUSTOMER",
                        request.getEmail(),
                        customerVariables,
                        qrImage
                );

            } catch (Exception emailException) {
                emailException.printStackTrace();
            }

            try {
                String productionEmail = "eternallegacynqr@gmail.com";

                Map<String, String> productionVariables
                        = buildCustomerEmailVariables(orderId);

                productionEmailSent = sendTemplateEmail(
                        orderId,
                        "PAYMENT_PRODUCTION",
                        productionEmail,
                        productionVariables,
                        qrImage
                );

            } catch (Exception emailException) {
                emailException.printStackTrace();
            }

            boolean emailSent
                    = customerEmailSent && productionEmailSent;

            // ============================================================
            // 17. RESPUESTA
            // ============================================================
            StringBuilder responseJson
                    = new StringBuilder();

            responseJson.append("{");

            responseJson.append(
                    "\"success\":true,");

            responseJson.append(
                    "\"pagoId\":")
                    .append(pagoId)
                    .append(",");

            responseJson.append(
                    "\"orderId\":")
                    .append(orderId)
                    .append(",");

            responseJson.append(
                    "\"paymentId\":")
                    .append(paymentId)
                    .append(",");

            responseJson.append(
                    "\"orderNumber\":\"")
                    .append(
                            escapeJson(orderNumber))
                    .append("\",");

            responseJson.append(
                    "\"transactionId\":\"")
                    .append(
                            escapeJson(
                                    request.getTransactionId()))
                    .append("\",");

            responseJson.append(
                    "\"total\":")
                    .append(
                            total.toPlainString())
                    .append(",");

            responseJson.append(
                    "\"currency\":\"")
                    .append(
                            escapeJson(currency))
                    .append("\",");

            responseJson.append(
                    "\"qrUrl\":\"")
                    .append(
                            escapeJson(qrUrl))
                    .append("\",");

            responseJson.append(
                    "\"emailSent\":")
                    .append(emailSent);

            if (!emailSent) {

                responseJson.append(
                        ",\"emailError\":\"")
                        .append(
                                escapeJson(
                                        emailError == null
                                                ? "Error enviando email"
                                                : emailError))
                        .append("\"");
            }

            responseJson.append(
                    ",\"message\":\"Pago registrado correctamente\"");

            responseJson.append("}");

            return Response.ok(
                    responseJson.toString())
                    .build();

        } catch (Exception e) {

            // ============================================================
            // ROLLBACK
            // ============================================================
            if (connection != null
                    && !committed) {

                try {

                    connection.rollback();

                } catch (SQLException rollbackException) {

                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

            return Response
                    .status(
                            Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            "{"
                            + "\"success\":false,"
                            + "\"message\":\""
                            + escapeJson(
                                    e.getMessage() == null
                                    ? "Error interno procesando pago"
                                    : e.getMessage())
                            + "\""
                            + "}")
                    .build();

        } finally {

            // ============================================================
            // CLOSE CONNECTION
            // ============================================================
            if (connection != null) {

                try {

                    connection.close();

                } catch (SQLException closeException) {

                    closeException.printStackTrace();
                }
            }
        }
    }

    private String generateOrderNumber() {

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String random
                = UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 6)
                        .toUpperCase();

        return "ELQR-" + date + "-" + random;
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    /**
     * ****
     */
    /* @POST
    @Path("/registerPago")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPago(RequestPago request) throws AddressException, WriterException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 1. Generar QR con URL única
            String qrText = "http://ec2-3-141-153-150.us-east-2.compute.amazonaws.com/" + request.getTransactionId();
            //"http://3.140.23.226:8080/eternal/api/v1/GetUrlsQR?transaccion_id" + request.getTransactionId();
            BufferedImage qrImage = QRGenerator.generateQR(qrText, 400, 400);

            // 2. Subir QR a S3
            String qrUrl = S3UploaderQR.uploadQRToS3(qrImage, "pago_" + request.getTransactionId());

            // 3. Guardar en BD
            Connection connection = DatabaseConnection.getConnection();

            String insertSQL = "INSERT INTO pagos (email, transaction_id, amount, url_qr) VALUES (?, ?, ?, ?) RETURNING id";
            stmt = connection.prepareStatement(insertSQL);
            stmt.setString(1, request.getEmail());
            stmt.setString(2, request.getTransactionId());
            stmt.setString(3, request.getAmount());
            stmt.setString(4, qrUrl);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int pagoId = rs.getInt("id");

            // 4. Enviar correo con QR
            sendEmailWithQR(request.getEmail(),
                    request.getTransactionId(),
                    request.getAmount(),
                    qrUrl,
                    qrImage);

            // 5. Retornar respuesta
            PagoResponse response = new PagoResponse(pagoId, qrUrl, "Pago registrado y correo enviado correctamente");
            return Response.ok(response).build();

        } catch (SQLException | IOException e) {
            //e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ignored) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }
     */
 /*  private boolean sendEmailWithQR(String recipient,
            String transactionId,
            String amount,
            String qrUrl,
            BufferedImage qrImage) {

        final String username = "eternallegacynqr@gmail.com";
        final String password = "dzlkpslwdiqylguo"; // App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            System.out.println("[INFO] Iniciando envío de correo a: " + recipient);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Eternal Legacy"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Confirmación de pago - Eternal Legacy");

            // Contenido HTML
            String htmlContent
                    = "<h3>Gracias por tu pago</h3>"
                    + "<p><b>Transacción:</b> " + transactionId + "</p>"
                    + "<p><b>Monto:</b> " + amount + "</p>"
                    + "<p>Puedes acceder a tu transacción aquí: "
                    + "<a href='" + qrUrl + "'>Ver QR</a></p>"
                    + "<p>También adjuntamos tu QR como imagen.</p>";

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(htmlContent, "text/html; charset=utf-8");

            // Imagen adjunta
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] qrBytes = baos.toByteArray();
            DataSource dataSource = new ByteArrayDataSource(new ByteArrayInputStream(qrBytes), "image/png");

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setDataHandler(new DataHandler(dataSource));
            imagePart.setFileName("QR_" + transactionId + ".png");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(imagePart);
            message.setContent(multipart);

            // Enviar correo
            Transport.send(message);

            System.out.println("[INFO] Correo enviado correctamente a " + recipient);
            return true;

        } catch (AuthenticationFailedException e) {
            System.err.println("[ERROR] Falló la autenticación SMTP. Revisa la App Password o habilita menos seguridad en Gmail.");
            e.printStackTrace();
        } catch (SendFailedException e) {
            System.err.println("[ERROR] Dirección de correo no válida o error al conectar con el servidor: " + recipient);
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("[ERROR] Error al construir o enviar el mensaje: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("[ERROR] Error al procesar la imagen del QR: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Excepción inesperada: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
     */
private boolean sendTemplateEmail(
            int orderId,
            String templateCode,
            String recipient,
            Map<String, String> variables,
            BufferedImage qrImage) {

        Connection emailConnection = null;

        String subject = null;

        try {

            // ============================================================
            // 1. CONEXIÓN INDEPENDIENTE PARA EMAIL
            // ============================================================
            emailConnection
                    = DatabaseConnection.getConnection();

            // ============================================================
            // 2. OBTENER TEMPLATE
            // ============================================================
            EmailTemplate template
                    = loadEmailTemplate(
                            emailConnection,
                            templateCode);

            if (template == null) {

                logEmail(
                        emailConnection,
                        orderId,
                        templateCode,
                        recipient,
                        null,
                        "ERROR",
                        "No existe template activo: "
                        + templateCode);

                return false;
            }

            // ============================================================
            // 3. REEMPLAZAR VARIABLES
            // ============================================================
            subject
                    = replaceTemplateVariables(
                            template.getSubject(),
                            variables);

            String html
                    = replaceTemplateVariables(
                            template.getHtmlContent(),
                            variables);

            // ============================================================
            // 4. ENVIAR EMAIL
            // ============================================================
            sendEmailSMTP(
                    recipient,
                    subject,
                    html,
                    qrImage);

            // ============================================================
            // 5. REGISTRAR EMAIL ENVIADO
            // ============================================================
            logEmail(
                    emailConnection,
                    orderId,
                    templateCode,
                    recipient,
                    subject,
                    "SENT",
                    null);

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            // ============================================================
            // 6. REGISTRAR ERROR
            // ============================================================
            try {

                if (emailConnection == null) {

                    emailConnection
                            = DatabaseConnection.getConnection();
                }

                logEmail(
                        emailConnection,
                        orderId,
                        templateCode,
                        recipient,
                        subject,
                        "ERROR",
                        e.getMessage());

            } catch (Exception logException) {

                logException.printStackTrace();
            }

            return false;

        } finally {

            if (emailConnection != null) {

                try {

                    emailConnection.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    private EmailTemplate loadEmailTemplate(
            Connection connection,
            String templateCode)
            throws SQLException {

        String sql
                = "SELECT template_code, subject, html_content "
                + "FROM email_templates "
                + "WHERE template_code = ? "
                + "AND active = TRUE "
                + "LIMIT 1";

        try (PreparedStatement stmt
                = connection.prepareStatement(sql)) {

            stmt.setString(1, templateCode);

            try (ResultSet rs
                    = stmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                EmailTemplate template
                        = new EmailTemplate();

                template.setTemplateCode(
                        rs.getString("template_code"));

                template.setSubject(
                        rs.getString("subject"));

                template.setHtmlContent(
                        rs.getString("html_content"));

                return template;
            }
        }
    }

    private String replaceTemplateVariables(
            String content,
            Map<String, String> variables) {

        if (content == null) {
            return "";
        }

        String result = content;

        for (Map.Entry<String, String> entry
                : variables.entrySet()) {

            String key
                    = "{{" + entry.getKey() + "}}";

            String value
                    = entry.getValue() == null
                    ? ""
                    : entry.getValue();

            result
                    = result.replace(
                            key,
                            value);
        }

        return result;
    }

    private void logEmail(
            Connection connection,
            int orderId,
            String emailType,
            String recipient,
            String subject,
            String status,
            String errorMessage) {

        String sql
                = "INSERT INTO email_log ("
                + "order_id, "
                + "email_type, "
                + "recipient, "
                + "subject, "
                + "status, "
                + "error_message, "
                + "sent_at"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt
                = connection.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    orderId);

            stmt.setString(
                    2,
                    emailType);

            stmt.setString(
                    3,
                    recipient);

            stmt.setString(
                    4,
                    subject);

            stmt.setString(
                    5,
                    status);

            if (errorMessage == null) {

                stmt.setNull(
                        6,
                        Types.VARCHAR);

            } else {

                stmt.setString(
                        6,
                        errorMessage);
            }

            if ("SENT".equals(status)) {

                stmt.setTimestamp(
                        7,
                        new Timestamp(
                                System.currentTimeMillis()));

            } else {

                stmt.setNull(
                        7,
                        Types.TIMESTAMP);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {

            /*
         * No lanzar excepción.
         *
         * Un problema registrando email_log
         * no debe tumbar un pago ya realizado.
             */
            e.printStackTrace();
        }
    }

    private Map<String, String> buildCustomerEmailVariables(
            int orderId)
            throws SQLException {

        Connection connection = null;

        Map<String, String> variables
                = new HashMap<>();

        try {

            connection
                    = DatabaseConnection.getConnection();

            String orderSQL
                    = "SELECT "
                    + "o.order_number, "
                    + "o.email, "
                    + "o.subtotal, "
                    + "o.shipping_cost, "
                    + "o.total, "
                    + "o.currency, "
                    + "p.transaction_id, "
                    + "p.qr_url "
                    + "FROM orders o "
                    + "LEFT JOIN payments p "
                    + "ON p.order_id = o.id "
                    + "WHERE o.id = ? "
                    + "LIMIT 1";

            try (PreparedStatement stmt
                    = connection.prepareStatement(
                            orderSQL)) {

                stmt.setInt(
                        1,
                        orderId);

                try (ResultSet rs
                        = stmt.executeQuery()) {

                    if (!rs.next()) {

                        throw new SQLException(
                                "No existe order_id: "
                                + orderId);
                    }

                    variables.put(
                            "ORDER_NUMBER",
                            safe(rs.getString(
                                    "order_number")));

                    variables.put(
                            "CUSTOMER_EMAIL",
                            safe(rs.getString(
                                    "email")));

                    variables.put(
                            "SUBTOTAL",
                            money(rs.getBigDecimal(
                                    "subtotal")));

                    variables.put(
                            "SHIPPING_COST",
                            money(rs.getBigDecimal(
                                    "shipping_cost")));

                    variables.put(
                            "TOTAL",
                            money(rs.getBigDecimal(
                                    "total")));

                    variables.put(
                            "CURRENCY",
                            safe(rs.getString(
                                    "currency")));

                    variables.put(
                            "TRANSACTION_ID",
                            safe(rs.getString(
                                    "transaction_id")));

                    variables.put(
                            "QR_URL",
                            safe(rs.getString(
                                    "qr_url")));

                    variables.put(
                            "CUSTOMER_NAME",
                            "Cliente EternalLegacyQR");
                }
            }

            // ========================================================
            // PRODUCTOS
            // ========================================================
            variables.put(
                    "PRODUCTS",
                    buildProductsHtml(
                            connection,
                            orderId));

            // ========================================================
            // ENTREGA
            // ========================================================
            variables.put(
                    "ESTIMATED_DELIVERY",
                    getEstimatedDelivery(
                            connection,
                            orderId));

            return variables;

        } finally {

            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String buildProductsHtml(
            Connection connection,
            int orderId)
            throws SQLException {

        String sql
                = "SELECT "
                + "product_name, "
                + "sku, "
                + "quantity, "
                + "unit_price, "
                + "total_price, "
                + "product_image_url, "
                + "product_format "
                + "FROM order_items "
                + "WHERE order_id = ? "
                + "ORDER BY id";

        StringBuilder html
                = new StringBuilder();

        try (PreparedStatement stmt
                = connection.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    orderId);

            try (ResultSet rs
                    = stmt.executeQuery()) {

                while (rs.next()) {

                    String productName
                            = safe(rs.getString(
                                    "product_name"));

                    String sku
                            = safe(rs.getString(
                                    "sku"));

                    int quantity
                            = rs.getInt("quantity");

                    String unitPrice
                            = money(rs.getBigDecimal(
                                    "unit_price"));

                    String totalPrice
                            = money(rs.getBigDecimal(
                                    "total_price"));

                    String imageUrl
                            = safe(rs.getString(
                                    "product_image_url"));

                    String format
                            = safe(rs.getString(
                                    "product_format"));

                    html.append(
                            "<div style=\""
                            + "border-bottom:1px solid #ddd;"
                            + "padding:15px 0;"
                            + "\">");

                    if (!imageUrl.isEmpty()) {

                        html.append(
                                "<img src=\"")
                                .append(
                                        escapeHtml(imageUrl))
                                .append(
                                        "\" "
                                        + "style=\""
                                        + "width:100px;"
                                        + "height:auto;"
                                        + "margin-bottom:10px;"
                                        + "\" />");
                    }

                    html.append(
                            "<div><strong>")
                            .append(
                                    escapeHtml(productName))
                            .append(
                                    "</strong></div>");

                    if (!sku.isEmpty()) {

                        html.append(
                                "<div>SKU: ")
                                .append(
                                        escapeHtml(sku))
                                .append(
                                        "</div>");
                    }

                    if (!format.isEmpty()) {

                        html.append(
                                "<div>Formato: ")
                                .append(
                                        escapeHtml(format))
                                .append(
                                        "</div>");
                    }

                    html.append(
                            "<div>Cantidad: ")
                            .append(quantity)
                            .append("</div>");

                    html.append(
                            "<div>Precio unitario: ")
                            .append(unitPrice)
                            .append("</div>");

                    html.append(
                            "<div>Total: ")
                            .append(totalPrice)
                            .append("</div>");

                    html.append(
                            "</div>");
                }
            }
        }

        return html.toString();
    }

    private String getEstimatedDelivery(
            Connection connection,
            int orderId)
            throws SQLException {

        String sql
                = "SELECT estimated_delivery_date "
                + "FROM order_shipping "
                + "WHERE order_id = ? "
                + "LIMIT 1";

        try (PreparedStatement stmt
                = connection.prepareStatement(sql)) {

            stmt.setInt(
                    1,
                    orderId);

            try (ResultSet rs
                    = stmt.executeQuery()) {

                if (rs.next()
                        && rs.getDate(
                                "estimated_delivery_date")
                        != null) {

                    return rs.getDate(
                            "estimated_delivery_date")
                            .toString();
                }
            }
        }

        return "Será informada posteriormente";
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }

    private String money(BigDecimal value) {

        if (value == null) {
            return "0.00";
        }

        return value
                .setScale(
                        2,
                        RoundingMode.HALF_UP)
                .toPlainString();
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    
    
private void sendEmailSMTP(
        String recipient,
        String subject,
        String html,
        BufferedImage qrImage) throws Exception {

    // ============================================================
    // CREDENCIALES DESDE VARIABLES DE ENTORNO
    // ============================================================

    final String smtpHost = "smtp.gmail.com";
    final String smtpPort = "587";

    final String smtpUsername = System.getenv("SMTP_USERNAME");
    final String smtpPassword = System.getenv("SMTP_PASSWORD");

    if (smtpUsername == null || smtpUsername.trim().isEmpty()) {
        throw new IllegalStateException(
                "Falta la variable de entorno requerida: SMTP_USERNAME"
        );
    }

    if (smtpPassword == null || smtpPassword.trim().isEmpty()) {
        throw new IllegalStateException(
                "Falta la variable de entorno requerida: SMTP_PASSWORD"
        );
    }

    final String senderEmail = smtpUsername.trim();

    // ============================================================
    // CONFIGURACIÓN SMTP
    // ============================================================

    Properties props = new Properties();

    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.ssl.trust", smtpHost);

    Session session = Session.getInstance(
            props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            senderEmail,
                            smtpPassword
                    );
                }
            }
    );

    // ============================================================
    // MENSAJE
    // ============================================================

    MimeMessage message = new MimeMessage(session);

    message.setFrom(
            new InternetAddress(senderEmail, "Eternal Legacy")
    );

    message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipient)
    );

    message.setSubject(
            subject,
            StandardCharsets.UTF_8.name()
    );

    MimeMultipart multipart = new MimeMultipart("related");

    // HTML del correo
    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(html, "text/html; charset=UTF-8");
    multipart.addBodyPart(htmlPart);

    // ============================================================
    // IMAGEN QR INLINE
    // ============================================================

    if (qrImage != null) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        boolean imageCreated = ImageIO.write(qrImage, "png", baos);

        if (!imageCreated) {
            throw new IOException(
                    "No fue posible convertir el QR a PNG"
            );
        }

        byte[] qrBytes = baos.toByteArray();

        DataSource dataSource = new ByteArrayDataSource(
                new ByteArrayInputStream(qrBytes),
                "image/png"
        );

        MimeBodyPart imagePart = new MimeBodyPart();

        imagePart.setDataHandler(new DataHandler(dataSource));
        imagePart.setHeader("Content-ID", "<qrCode>");
        imagePart.setDisposition(MimeBodyPart.INLINE);

        multipart.addBodyPart(imagePart);
    }

    message.setContent(multipart);

    // No registrar contraseñas ni otros secretos.
    System.out.println("[INFO] Enviando correo a: " + recipient);

    Transport.send(message);

    System.out.println(
            "[INFO] Correo enviado correctamente a: " + recipient
    );
}


 /*   private void sendEmailSMTP(
        String recipient,
        String subject,
        String html,
        BufferedImage qrImage) throws Exception {

    final String smtpHost = "smtp.gmail.com";
    final String smtpPort = "587";

    final String senderEmail = "eternallegacynqr@gmail.com";

    // TODO: mover posteriormente a variable de entorno
    final String senderPassword = "TU_APP_PASSWORD";

    Properties props = new Properties();

    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.ssl.trust", smtpHost);

    Session session = Session.getInstance(
            props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            senderEmail,
                            senderPassword
                    );
                }
            }
    );

    MimeMessage message = new MimeMessage(session);

    message.setFrom(
            new InternetAddress(
                    senderEmail,
                    "Eternal Legacy"
            )
    );

    message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipient)
    );

    message.setSubject(
            subject,
            StandardCharsets.UTF_8.name()
    );

    MimeMultipart multipart =
            new MimeMultipart("related");

    // ============================================================
    // HTML
    // ============================================================

    MimeBodyPart htmlPart =
            new MimeBodyPart();

    htmlPart.setContent(
            html,
            "text/html; charset=UTF-8"
    );

    multipart.addBodyPart(htmlPart);

    // ============================================================
    // QR INLINE
    // ============================================================

    if (qrImage != null) {

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        boolean imageCreated =
                ImageIO.write(
                        qrImage,
                        "png",
                        baos
                );

        if (!imageCreated) {
            throw new IOException(
                    "No fue posible convertir el QR a PNG"
            );
        }

        byte[] qrBytes =
                baos.toByteArray();

        DataSource dataSource =
                new ByteArrayDataSource(
                        new ByteArrayInputStream(qrBytes),
                        "image/png"
                );

        MimeBodyPart imagePart =
                new MimeBodyPart();

        imagePart.setDataHandler(
                new DataHandler(dataSource)
        );

        imagePart.setHeader(
                "Content-ID",
                "<qrCode>"
        );

        imagePart.setDisposition(
                MimeBodyPart.INLINE
        );

        multipart.addBodyPart(imagePart);
    }

    // ============================================================
    // CONTENIDO DEL MENSAJE
    // ============================================================

    message.setContent(multipart);

    // ============================================================
    // ENVIAR
    // ============================================================

    System.out.println(
            "[INFO] Enviando correo a: " + recipient
    );

    Transport.send(message);

    System.out.println(
            "[INFO] Correo enviado correctamente a: "
            + recipient
    );
}
*/
}//principal
