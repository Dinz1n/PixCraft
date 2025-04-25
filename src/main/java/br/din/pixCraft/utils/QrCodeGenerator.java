package br.din.pixCraft.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public class QrCodeGenerator {
    public static BufferedImage generateQrImage(String qrCodeData, int size) {
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}