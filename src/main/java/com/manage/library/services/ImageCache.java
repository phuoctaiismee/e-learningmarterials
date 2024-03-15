package com.manage.library.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class ImageCache {

    private static final HashMap<String, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage getImage(String path) {
        BufferedImage image = imageCache.get(path);
        if (image == null) {
            // Nếu hình ảnh chưa được cache, tải và cache nó
            try {
                File file = new File(path);
                image = ImageIO.read(file);
                imageCache.put(path, image);
            } catch (IOException e) {
                e.printStackTrace();
                // Xử lý khi không thể tải hình ảnh
            }
        }
        return image;
    }

    public static void clearCache() {
        imageCache.clear();
    }
}
