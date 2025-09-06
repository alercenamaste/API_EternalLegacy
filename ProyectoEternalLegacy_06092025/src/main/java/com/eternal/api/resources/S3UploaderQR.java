/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

public class S3UploaderQR {
    
    private static final String BUCKET_NAME = "contenedoreternallegacyqrinicio";
    private static final String FOLDER = "imagenes_qr_cargadas/";
    
   // private static final String BUCKET_NAME = "contenedoreternallegacyqrinicio";
    private static final Region REGION = Region.US_EAST_2; // Región de Ohio
    
    // ⚠️ Mejor usar IAM Role o variables de entorno
    //private static final String ACCESS_KEY = "TU_ACCESS_KEY";
    //private static final String SECRET_KEY = "TU_SECRET_KEY";


   public static String uploadQRToS3(BufferedImage qrImage, String fileName) throws IOException {
    try {
        // Convertir BufferedImage → byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        byte[] qrBytes = baos.toByteArray();

        // Crear cliente S3 con DefaultCredentialsProvider (sin ACCESS_KEY/SECRET_KEY en código)
        S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_2) // tu región
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        // Definir el nombre del objeto en S3
        String objectKey = FOLDER + fileName + ".png";

        // Crear request de subida
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("contenedoreternallegacyqrinicio") // tu bucket
                .key(objectKey)
                .contentType("image/png")
                .build();

        // Subir archivo
        s3.putObject(putObjectRequest, RequestBody.fromBytes(qrBytes));

        // Retornar la URL pública
        return "https://contenedoreternallegacyqrinicio.s3.us-east-2.amazonaws.com/" + objectKey;

    } catch (IOException e) {
        e.printStackTrace();
        return "Error procesando imagen: " + e.getMessage();
    } catch (Exception e) {
        e.printStackTrace();
        return "Error general: " + e.getMessage();
    }
}
}//principal
