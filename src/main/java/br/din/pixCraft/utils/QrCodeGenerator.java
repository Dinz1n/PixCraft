package br.din.pixCraft.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class QrCodeGenerator {

    /**
     * Converte uma string Base64 em uma imagem BufferedImage.
     * @param base64String A string em Base64 do QR Code.
     * @return A imagem BufferedImage gerada.
     */
    public static BufferedImage generateQrImage(String base64String) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}