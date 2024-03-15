package com.manage.library.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class USBSerialNumber {

    public List<String> getSerialByCommand () throws IOException {
        // Tạo đối tượng Runtime
        Runtime rt = Runtime.getRuntime();

        // Khởi tạo đối tượng StringBuilder
        StringBuilder sbd = new StringBuilder();

        // Truy vấn bằng đối tượng Process
        // Lọc ra những diskdrive có type là USB
        // Lấy ra serial number
        Process process = rt.exec(new String[]{"CMD", "/C", "WMIC diskdrive where InterfaceType='USB' get serialnumber"});

        // Khởi tạo BufferedReader để đọc nội dụng từ process
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // Khởi tạo danh sách để lưu trữ mã serial
        List<String> serialNumbers = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            // Loại bỏ khoảng trắng và kiểm tra xem dòng có chứa mã serial không
            String serialNumber = line.trim().replace("SerialNumber", "");
            if (!serialNumber.isEmpty()) {
                // Thêm mã serial vào danh sách
                serialNumbers.add(serialNumber);
            }
        }

        reader.close();
        return serialNumbers;
    }
    
    public static void main(String[] args) {
        USBSerialNumber se = new USBSerialNumber();
        try {
            List<String> list = se.getSerialByCommand();
            for (String string : list) {
                System.out.println("String: "+ string);
            }
        } catch (IOException ex) {
            
        }
    }
}
