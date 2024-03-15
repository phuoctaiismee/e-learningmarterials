package com.manage.library.utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class XImage {

    public static Image getAppIcon() {
        try (InputStream inputStream = XImage.class.getClassLoader().getResourceAsStream("logos/icons/gd.png")) {
            if (inputStream == null) {
                throw new RuntimeException("Image file not found: ");
            }

            return ImageIO.read(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(File src) {
        File dst = new File("logos", src.getName());
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        try {
            Path from = Paths.get(src.getAbsolutePath());
            Path to = Paths.get(dst.getAbsolutePath());
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ImageIcon read(String fileName) {
        try (InputStream inputStream = XImage.class.getClassLoader().getResourceAsStream("logos/" + fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Image file not found: " + fileName);
            }

            byte[] imageBytes = inputStream.readAllBytes();
            return new ImageIcon(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ImageIcon readIconSubject(String fileName) {
        try (InputStream inputStream = XImage.class.getClassLoader().getResourceAsStream("subject/" + fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Image file not found: " + fileName);
            }else{
                System.out.println("doo: " + inputStream);
            }

            byte[] imageBytes = inputStream.readAllBytes();
            return new ImageIcon(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
