/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class QRGenerator {

    public static BufferedImage generateQR(String baseUrl, 
                                          int width, 
                                          int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // URL basada solo en transaccion_id
        String qrText = baseUrl;
        
        // Configuración QR
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, width, height, hints);

        // Blanco y negro simple
        int qrColor = 0xFF000000; // negro
        int bgColor = 0xFFFFFFFF; // blanco

        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? qrColor : bgColor);
            }
        }

        return qrImage;
    }
}




/**
 *
 * @author mauricio.olguin
 
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

public class QRGenerator {
    
    // Genera un QR como imagen en memoria
    public static BufferedImage generateQR(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
*/