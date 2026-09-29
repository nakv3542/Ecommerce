package com.example.forfinal.service;

import com.example.forfinal.config.BakongConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
public class BakongService {

    @Autowired
    private BakongConfig bakongConfig;

    private static final int CURRENCY_KHR = 116;
    private static final int CURRENCY_USD = 840;

    public String generateKHQRPayload(double amount) {
        StringBuilder sb = new StringBuilder();

        appendTLV(sb, "00", "01");
        appendTLV(sb, "01", "12");

        String merchantInfo = buildMerchantAccountInfo();
        appendTLV(sb, "31", merchantInfo);

        appendTLV(sb, "52", bakongConfig.getMerchantCategoryCode());

        int currencyCode = "US".equalsIgnoreCase(bakongConfig.getCurrency()) ? CURRENCY_USD : CURRENCY_KHR;
        appendTLV(sb, "53", String.format("%03d", currencyCode));

        appendTLV(sb, "54", String.format("%.2f", amount));
        appendTLV(sb, "58", "KH");
        appendTLV(sb, "59", truncate(bakongConfig.getMerchantName(), 25));
        appendTLV(sb, "60", truncate(bakongConfig.getMerchantCity(), 15));

        String withoutCRC = sb.toString();
        String crc = calculateCRC(withoutCRC + "6304");
        sb.append("6304").append(crc);

        return sb.toString();
    }

    private String buildMerchantAccountInfo() {
        StringBuilder mb = new StringBuilder();
        appendTLV(mb, "00", "khqr.bakong.com");
        appendTLV(mb, "01", bakongConfig.getAccountId());
        return mb.toString();
    }

    private void appendTLV(StringBuilder sb, String tag, String value) {
        sb.append(tag);
        sb.append(String.format("%02d", value.length()));
        sb.append(value);
    }

    private String truncate(String value, int maxLen) {
        if (value == null) return "";
        byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (bytes.length <= maxLen) return value;
        return value.substring(0, Math.min(value.length(), maxLen));
    }

    public String calculateCRC(String payload) {
        int crc = 0xFFFF;
        byte[] data = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        for (byte b : data) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
            }
            crc &= 0xFFFF;
        }
        return String.format("%04X", crc);
    }

    public String generateQRCodeImage(String payload, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(
                qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, width, height, hints));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
