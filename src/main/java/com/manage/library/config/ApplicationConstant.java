package com.manage.library.config;

import java.io.File;

/**
 *
 * @author PC
 */
public class ApplicationConstant {

    public static class ResourceType {

        public static final int FOLDER = 1;
        public static final int IMAGE = 2;
        public static final int VIDEO = 3;
        public static final int DOC = 4;
    }

    public static class PathConfig {

        public static final String APPLICATION_BIN = "bin";
        public static final String APPLICATION_PROPERTIES = APPLICATION_BIN + File.separator + "application.properties";
        public static final String APPLICATION_DATABASE = APPLICATION_BIN + File.separator + "application.properties";
        public static final String CONFIG_PROPERTIES = "config.properties";
    }

    public static class Key {

        public static final String KEY_DESTRING = "gdvn-serect-key";
    }

}
