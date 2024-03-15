package com.manage.library.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jasypt.util.text.BasicTextEncryptor;

public class JasptyCryption {

    private final Properties properties;
    private final char[] secretKey;

    public JasptyCryption(String propertyFilePath, String secretKey) {
        this.properties = initProperties(propertyFilePath);
        this.secretKey = secretKey.toCharArray();
    }

    /*
     * TODO: Thực hiện truy vấn và chuyển đổi file application.properties thành đối tượng Properties.
     * 
     * PARAM:
     *   @path: Đường dẫn đến file properties.
     * RETURN:
     *   Đối tượng Properties chứa thông tin từ file properties.
     * 
     */
    private Properties initProperties(String path) {
        Properties properties = new Properties();
        try {

            File file = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            InputStream input = new FileInputStream(file);

            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    /*
     *
     * TODO: Mã hóa kí tự từ một chuỗi text
     *
     * PARAM:
     *  @text Nội dung cần mã hóa
     * RETURN:
     *   Chuỗi kí tự đã mã hóa
     *
     */ /*
     *
     * TODO: Mã hóa kí tự từ một chuỗi text
     *
     * PARAM:
     *  @text Nội dung cần mã hóa
     * RETURN:
     *   Chuỗi kí tự đã mã hóa
     *
     */
    private String encode(String text) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(secretKey);
        return textEncryptor.encrypt(text);
    }

    /*
     *
     * TODO: Giãi mã chuỗi mã hóa từ key của properties (Mỗi key chứa một chuối mã hóa cụ thể)
     *
     * PARAM:
     *  @key Mã định danh cho usb
     *     usb.vendor VendorID của USB
     *     usb.product ProductID của USB
     *
     * RETURN:
     *   Chuỗi kí tự đã giải mã
     *
     */
    private String decode(String key) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(secretKey);
        return textEncryptor.decrypt(key);
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String encodeProperty(String key) {
        String value = properties.getProperty(key);
        return encode(value);
    }

    public String decodeProperty(String key) {
        String encryptedValue = properties.getProperty(key);
        return decode(encryptedValue);
    }

}
