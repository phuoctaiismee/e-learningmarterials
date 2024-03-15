package com.manage.library.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PDFThumbnail {

    private static final Map<String, BufferedImage> thumbnailCache = new ConcurrentHashMap<>();

    public static BufferedImage generateThumbnail(String pdfFilePath, int width, int height) {
        try {
            // Kiểm tra xem hình ảnh đã được cache chưa
            BufferedImage cachedThumbnail = thumbnailCache.get(pdfFilePath);
            if (cachedThumbnail != null) {
                return cachedThumbnail;
            }

            // Load tệp PDF
            PDDocument document = PDDocument.load(new File(pdfFilePath));

            // Tạo PDFRenderer từ PDDocument
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Rút trích hình ảnh từ trang đầu tiên
            BufferedImage originalImage = pdfRenderer.renderImageWithDPI(0, 300); // DPI 300, bạn có thể điều chỉnh tùy ý

            // Thay đổi kích thước hình ảnh theo thông số chiều dài và chiều rộng
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            // Lưu hình ảnh vào cache
            thumbnailCache.put(pdfFilePath, bufferedImage);

            // Đóng tài liệu
            document.close();

            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String pdfFilePath = "C:\\Users\\rifud\\Downloads\\CV_NGUYENPHUOCTAI_Front-End Developer (1).pdf";
        int thumbnailWidth = 300; // Chiều rộng của thumbnail
        int thumbnailHeight = 300; // Chiều dài của thumbnail
        BufferedImage thumbnail = generateThumbnail(pdfFilePath, thumbnailWidth, thumbnailHeight);
        if (thumbnail != null) {
            displayThumbnail(thumbnail);
        } else {
            System.out.println("Failed to generate thumbnail.");
        }
    }

    private static void displayThumbnail(BufferedImage thumbnail) {
        JFrame frame = new JFrame("PDF Thumbnail Viewer");
        JLabel label = new JLabel(new ImageIcon(thumbnail));

        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(thumbnail.getWidth(), thumbnail.getHeight());
        frame.setVisible(true);
    }
}
