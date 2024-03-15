package com.manage.library.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ThumbnailGenerate {

    public static BufferedImage createThumbnail(String imagePath, int width, int height) throws IOException {
        File file = new File(imagePath);
        return createThumbnailWithMultithreading(file, width, height);
    }

    public static BufferedImage createThumbnail(File file, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = thumbnail.createGraphics();
        g2d.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_FAST), 0, 0, null);
        g2d.dispose();

        return thumbnail;
    }

    public static BufferedImage createThumbnailWithMultithreading(File file, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int numThreads = Runtime.getRuntime().availableProcessors(); // Get number of available processors
        Thread[] threads = new Thread[numThreads];
        int chunkSize = originalImage.getHeight() / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int startY = threadIndex * chunkSize;
                int endY = (threadIndex == numThreads - 1) ? originalImage.getHeight() : (threadIndex + 1) * chunkSize;
                Graphics2D g2d = thumbnail.createGraphics();
                g2d.drawImage(originalImage.getSubimage(0, startY, originalImage.getWidth(), endY - startY).getScaledInstance(width, endY - startY, Image.SCALE_FAST), 0, startY * height / originalImage.getHeight(), null);
                g2d.dispose();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return thumbnail;
    }
}
