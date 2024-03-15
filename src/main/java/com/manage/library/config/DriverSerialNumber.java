package com.manage.library.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class DriverSerialNumber {

    public List<String> getVolumeSerialNumbers() {
        List<String> serialNumbers = new ArrayList<>();

        try {
            //Lấy danh sách tất cả ổ đĩa có trên máy
            for (FileStore store : FileSystems.getDefault().getFileStores()) {
                //Ex: SIMPLE (F:)
                String storeString = store.toString();
                // Cắt chuỗi lấy tên ổ (F:)
                String volumeName = storeString.substring(storeString.lastIndexOf("(") + 1, storeString.lastIndexOf(")")).trim();
                // Khởi tạo dòng lệnh
                String command = "cmd /c vol " + volumeName;
                // Run cmd với dòng lệnh trên -> Serial Vol
                String serialNumber = getVolumeSerialNumber(command);
                // Thêm vào mảng
                serialNumbers.add(serialNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serialNumbers;
    }

    private static String getVolumeSerialNumber(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder serialNumber = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.contains("Serial Number")) {
                serialNumber.append(line.substring(line.lastIndexOf(" ") + 1).trim());
            }
        }
        return serialNumber.toString();
    }

    public static void main(String[] args) {
        DriverSerialNumber driver = new DriverSerialNumber();
        List<String> serialNumbers = driver.getVolumeSerialNumbers();
        for (String serialNumber : serialNumbers) {
            System.out.println("Serial Number: " + serialNumber);
        }
    }
}
