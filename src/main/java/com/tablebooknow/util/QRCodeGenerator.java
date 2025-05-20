package com.tablebooknow.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class QRCodeGenerator {

    public static byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        System.out.println("QR Code generated for: " + text);
        System.out.println("QR Code dimensions: " + width + "x" + height);

        return outputStream.toByteArray();
    }

    public static String createQRCodeContent(String reservationId, String paymentId, String userId) {
        // Create a JSON-like string with the relevant information
        return String.format("{\"reservationId\":\"%s\",\"paymentId\":\"%s\",\"userId\":\"%s\",\"timestamp\":\"%s\"}",
                reservationId, paymentId, userId, System.currentTimeMillis());
    }

    public static String createQRCodeBase64(String text) throws WriterException, IOException {
        return createQRCodeBase64(text, 200, 200);
    }

    public static String createQRCodeBase64(String text, int width, int height) throws WriterException, IOException {
        byte[] qrCodeBytes = generateQRCodeImage(text, width, height);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    public static void saveQRCodeImage(String text, String filePath, int width, int height) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        System.out.println("QR Code saved to: " + filePath);
    }

    public static BufferedImage generateQRCodeImage2(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static boolean saveQRCodeToFile(String text, String filePath, int width, int height)
            throws WriterException, IOException {
        try {
            BufferedImage image = generateQRCodeImage2(text, width, height);
            File qrFile = new File(filePath);
            ImageIO.write(image, "PNG", qrFile);

            System.out.println("QR Code saved to file: " + filePath);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving QR code to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveQRCodeToFile(String text, String filePath, int width, int height, String imageFormat)
            throws WriterException, IOException {
        try {
            BufferedImage image = generateQRCodeImage2(text, width, height);
            File qrFile = new File(filePath);
            ImageIO.write(image, imageFormat, qrFile);

            System.out.println("QR Code saved to file: " + filePath);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving QR code to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void generateQRCodeToStream(String text, FileOutputStream outputStream, int width, int height)
            throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        System.out.println("QR Code written to output stream");
    }
}