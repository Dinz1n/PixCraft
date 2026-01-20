package br.com.din.pixcraft.qrmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

public final class QrCode {
    private final BitMatrix matrix;
    private final BufferedImage image;
    private final byte[][] raw;

    public QrCode(String text, int size) {
        this.matrix = generateMatrix(text, size);
        this.image = toImage(matrix);
        this.raw = toBinaryArray(matrix);
    }

    public BitMatrix getMatrix() { return matrix; }
    public BufferedImage getImage() { return image; }
    public byte[][] getRaw() { return raw; }

    private BitMatrix generateMatrix(String text, int size) {
        try {
            return new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage toImage(BitMatrix matrix) {
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    private byte[][] toBinaryArray(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        byte[][] out = new byte[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                out[y][x] = (byte) (matrix.get(x, y) ? 1 : 0);
            }
        }
        return out;
    }
}