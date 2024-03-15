package com.manage.library.config;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;

public class CryptionFileAndFolder {

    public final Properties properties;
    public final SecretKey secretkey;
    public final byte[] FIXED_IV;
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public CryptionFileAndFolder(String propertyFilePath, String secretKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.properties = initProperties(propertyFilePath);
        this.FIXED_IV = (properties.getProperty(secretKey)).getBytes();
        this.secretkey = generateKey(properties.getProperty(secretKey), FIXED_IV);
//        System.out.println(Arrays.toString(properties.getProperty(secretKey).getBytes()));
    }

    /* TODO: Thực hiện truy vấn và chuyển đổi file application.properties thành đối tượng Properties.
     * 
     * PARAM:
     *   @path: Đường dẫn đến file properties.
     * RETURN:
     *   Đối tượng Properties chứa thông tin từ file properties.
     * 
     */
    private Properties initProperties(String path) {
        Properties properties = new Properties();
        try (InputStream input = JasptyCryption.class.getClassLoader().getResourceAsStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /* TODO: Hàm khởi tạo một secrect key dựa trên một chuỗi text.
     * 
     * PARAM:
     *   @secret: key của serect trong file properties.
     * RETURN:
     *   Đối tượng SecretKey.
     * 
     */
    public static SecretKey generateKey(String secret, byte[] iv) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = iv;

        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /* TODO: Hàm mã hóa một file.
     * 
     * PARAM:
     *   @inputFilePath: Đường dẫn file cần mã hóa.
     *   @outputFilePath: Đường dẫn đến thư mục đầu ra của file mã hóa.
     *   @secretKey: SecretKey được khởi tạo từ hàm trên.
     * 
     */
    public static void encryptFile(String inputFilePath, String outputFilePath, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        try (FileInputStream inputStream = new FileInputStream(inputFilePath); FileOutputStream outputStream = new FileOutputStream(outputFilePath); CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /* TODO: Hàm giải mã một file mã hóa.
     * 
     * PARAM:
     *   @inputFilePath: Đường dẫn file cần giải mã.
     *   @outputFilePath: Đường dẫn đến thư mục đầu ra của file.
     *   @secretKey: SecretKey được khởi tạo từ hàm trên.
     */
    public static void decryptFile(String inputFilePath, String outputFilePath, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        try (
                FileInputStream inputStream = new FileInputStream(inputFilePath); CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher); FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error during decryption. Check key, IV, and padding.", e);
        }
    }

    /* TODO: Hàm giải mã một file mã hóa (tạm thời -> sẽ xóa khi đóng ứng dụng).
     * 
     * PARAM:
     *   @inputFilePath: Đường dẫn file cần giải mã.
     *   @secretKey: SecretKey được khởi tạo từ hàm trên.
     * 
     */
    public static File decryptToFile(String inputFilePath, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        try (InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + File.separator + "subject" + File.separator + inputFilePath)) {

            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buffer)) >= 0) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                byte[] decryptedData = byteArrayOutputStream.toByteArray();

                String fullName = FilenameUtils.getName(inputFilePath).replace(".encrypted", "");

                String pattern = "MMddyyyyHHmmss";  // Định dạng mới không chứa dấu cách và hai chấm

                DateFormat df = new SimpleDateFormat(pattern);

                Date today = Calendar.getInstance().getTime();

                String baseName = FilenameUtils.getBaseName(fullName).length() <= 3
                        ? FilenameUtils.getBaseName(fullName) + df.format(today)
                        : FilenameUtils.getBaseName(fullName);

                File tempFile = File.createTempFile(baseName, "." + FilenameUtils.getExtension(fullName));

                // Ghi dữ liệu giải mã vào tệp tạm thời
                Files.write(tempFile.toPath(), decryptedData);

                // Đảm bảo rằng tệp sẽ bị xóa khi ứng dụng tắt
                tempFile.deleteOnExit();

                return tempFile;
            }
        } catch (Exception e) {
            e.printStackTrace();



            throw new Exception("Lỗi trong quá trình giải mã. Kiểm tra khóa, IV và padding.", e);
        }
    }

    /* TODO: Hàm mã hóa một thư muc(Mã hóa toàn bộ file bên trong thư mục).
     * 
     * PARAM:
     *   @folderPath: Đường dẫn đến thư mục cần mã hóa.
     *   @secretKey: SecretKey được khởi tạo từ hàm trên.
     * 
     */
    public static void encryptFolder(String folderPath, SecretKey secretKey, byte[] iv) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();

                    // Kiểm tra phần mở rộng của file trước khi mã hóa
                    if (!fileName.endsWith(".encrypted")) {
                        String encryptedFilePath = file.getAbsolutePath() + ".encrypted";

                        // Kiểm tra xem file mã hóa đã tồn tại hay không
                        if (Files.exists(Paths.get(encryptedFilePath))) {
                            Files.delete(Paths.get(encryptedFilePath));
                        }

                        encryptFile(file.getAbsolutePath(), encryptedFilePath, secretKey, iv);

                        // Sau khi mã hóa xong, bạn có thể xóa file gốc nếu cần
                        Files.delete(Paths.get(file.getAbsolutePath()));
                    }
                } else {
                    encryptFolder(file.getAbsolutePath(), secretKey, iv);
                }
            }
        }
    }


    /* TODO: Hàm giải mã một thư muc(Giải mã toàn bộ file bên trong thư mục).
     * 
     * PARAM:
     *   @folderPath: Đường dẫn đến thư mục cần giải mã.
     *   @secretKey: SecretKey được khởi tạo từ hàm trên.
     * 
     */
    public static void decryptFolder(String folderPath, SecretKey secretKey, byte[] iv) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    decryptFolder(file.getAbsolutePath(), secretKey, iv);
                } else if (file.isFile() && file.getName().endsWith(".encrypted")) {
                    String decryptedFilePath = file.getAbsolutePath().replace(".encrypted", "");
                    decryptFile(file.getAbsolutePath(), decryptedFilePath, secretKey, iv);
                    Files.delete(Paths.get(file.getAbsolutePath()));
                }
            }
        }
    }
}
