
package com.manage.library.utils;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ThumbnailGenerator {

    public static BufferedImage createThumbnail(String imagePath, int targetWidth, int targetHeight) throws IOException {
        File imageFile = new File(imagePath);

        // Load a subsampled version of the image
        BufferedImage originalImage = loadImage(imageFile);

        // Scale the downsampled image to create a thumbnail
        return scaleImage(originalImage, targetWidth, targetHeight);
    }

    private static BufferedImage loadImage(File file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate the scaling factor based on the longer side of the image
        double scale = 1.0;
        if (originalWidth > originalHeight) {
            scale = (double) originalWidth / originalHeight;
            originalWidth = 800; // Limit the maximum dimension
            originalHeight = (int) (originalWidth / scale);
        } else {
            scale = (double) originalHeight / originalWidth;
            originalHeight = 800; // Limit the maximum dimension
            originalWidth = (int) (originalHeight / scale);
        }

        // Create a subsampled image
        Image scaledImage = originalImage.getScaledInstance(originalWidth, originalHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(originalWidth, originalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    private static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage thumbnail = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return thumbnail;
    }

    public static void main(String[] args) {
        try {
            String imagePath = "C:\\Users\\rifud\\Downloads\\g.jpg"; // Replace with the path to your image file
            int targetWidth = 100;
            int targetHeight = 100;
            BufferedImage thumbnail = createThumbnail(imagePath, targetWidth, targetHeight);

            // Display the thumbnail
            // For example, you can use JLabel to display the thumbnail in a Swing application
            JLabel thumbnailLabel = new JLabel(new ImageIcon(thumbnail));
            JFrame frame = new JFrame();
            frame.add(thumbnailLabel);
            frame.pack();
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
