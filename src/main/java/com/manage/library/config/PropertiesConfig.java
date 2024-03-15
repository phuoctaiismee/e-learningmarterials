package com.manage.library.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author PC
 */
public class PropertiesConfig {

    public static Properties loadProperties(String path) {
        Properties properties = new Properties();
        try (InputStream input = PropertiesConfig.class.getClassLoader().getResourceAsStream(path)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new IOException("Unable to find file: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
