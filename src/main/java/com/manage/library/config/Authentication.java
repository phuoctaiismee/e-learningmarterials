package com.manage.library.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author PC
 */
public class Authentication {

    private final Properties properties;

    public Authentication() {
        this.properties = PropertiesConfig.loadProperties("config.properties");
    }

    public boolean isAuthenticationConfirm(String pass) {
        String key = properties.getProperty("admin.serect.key");
        return pass.endsWith(key);
    }
}
