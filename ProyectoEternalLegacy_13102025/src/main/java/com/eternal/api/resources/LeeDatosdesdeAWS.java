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

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.regions.Region;

import com.eternal.api.resources.QRGenerator;
import com.eternal.api.resources.S3UploaderQR;
import com.google.zxing.WriterException;

//
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.message.internal.DataSourceProvider.ByteArrayDataSource;
@Path("/")
public class LeeDatosdesdeAWS {

    private static final String DB_URL = "jdbc:postgresql://database-eternallegacyqr.cv84auss2hlm.us-east-2.rds.amazonaws.com:5432/eternallegacyqr";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "01Febrero1981";
    private static final String BUCKET_NAME = "contenedoreternallegacyqr";
    
        
    //    https://contenedoreternallegacyqr.s3.us-east-2.amazonaws.com/contenedoreternallegacy_usuarios/
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
    String sql = "SELECT t3.url FROM PAGOS T1, USERSREGISTER T2, usersregistercontenido T3 " +
                 "WHERE T1.transaction_id = ? AND T2.email = T1.email AND T3.email = T2.email";

    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
        String url = request.getUrl();
        String email = request.getEmail();

        if (url == null || url.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"URL is required\"}")
                    .build();
        }

        try {
           // https://contenedoreternallegacyqr.s3.us-east-2.amazonaws.com/contenedoreternallegacy_inicial/3a691552-8a5b-43b3-ad88-241e453f3bb3.webp
            String bucketUrlPrefix = "https://contenedoreternallegacyqr.s3.us-east-2.amazonaws.com/";
            String key = url.replace(bucketUrlPrefix, "");
            String bucketName = BUCKET_NAME + "contenedoreternallegacy_usuarios/";

            // 1. Crear cliente S3 con SDK v2
            S3Client s3 = S3Client.builder()
                    .region(Region.US_EAST_2)
                    .build();

            // 2. Borrar el objeto
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3.deleteObject(deleteRequest);

            // 3. Borrar de PostgreSQL
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "DELETE FROM usersregisterContenido WHERE url = ? AND email = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, url);
                    stmt.setString(2, email);
                    stmt.executeUpdate();
                }
            }

            return Response.ok("{\"message\":\"File deleted successfully\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    
@POST
@Path("/registerPago")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response registerPago(RequestPago request) throws AddressException, WriterException {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
        // 1. Generar QR con URL única
        String qrText = "http://18.116.83.78/"+ request.getTransactionId(); //"http://3.140.23.226:8080/eternal/api/v1/GetUrlsQR?transaccion_id" + request.getTransactionId();
        BufferedImage qrImage = QRGenerator.generateQR(qrText, 400, 400);

        // 2. Subir QR a S3
        String qrUrl = S3UploaderQR.uploadQRToS3(qrImage, "pago_" + request.getTransactionId());
        
        // 3. Guardar en BD
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String insertSQL = "INSERT INTO pagos (email, transaction_id, amount, url_qr) VALUES (?, ?, ?, ?) RETURNING id";
        stmt = conn.prepareStatement(insertSQL);
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

    } catch (SQLException | IOException | MessagingException e) {
        //e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}")
                .build();
    } finally {
        try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }
}

private void sendEmailWithQR(String recipient, 
                             String transactionId, 
                             String amount, 
                             String qrUrl, 
                             BufferedImage qrImage) 
        throws MessagingException, IOException, AddressException {

    // Configuración SMTP (ejemplo con Gmail, cambiar según tu proveedor)
    final String username = "eternallegacynqr@gmail.com";
    final String password = "wkcmvlencnqikcww";  // usa App Password si es Gmail

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

    // Crear el mensaje
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject("Confirmación de pago - Eternal Legacy");

    // Cuerpo del mensaje con HTML
    String htmlContent = "<h3>Gracias por tu pago</h3>"
            + "<p><b>Transacción:</b> " + transactionId + "</p>"
            + "<p><b>Monto:</b> " + amount + "</p>"
            + "<p>Puedes acceder a tu transacción aquí: "
            + "<a href='" + qrUrl + "'>Ver QR</a></p>"
            + "<p>También adjuntamos tu QR como imagen.</p>";

    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent(htmlContent, "text/html; charset=utf-8");

    // Adjuntar imagen QR
    MimeBodyPart imagePart = new MimeBodyPart();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(qrImage, "png", baos);
    byte[] qrBytes = baos.toByteArray();
    DataSource dataSource = new ByteArrayDataSource(new ByteArrayInputStream(qrBytes), "image/png");
    imagePart.setDataHandler(new DataHandler(dataSource));
    imagePart.setFileName("QR_" + transactionId + ".png");

    // Combinar todo
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(textPart);
    multipart.addBodyPart(imagePart);

    message.setContent(multipart);

    // Enviar
    Transport.send(message);
}

    
    
}//principal
