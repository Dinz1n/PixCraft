package br.com.din.pixcraft.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

public class QrCodeGenerator {
    public static BufferedImage generate(String data, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            return MatrixToImageWriter.toBufferedImage(qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height));
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}