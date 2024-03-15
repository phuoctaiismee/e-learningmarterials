package com.manage.library.utils;

import com.manage.library.config.ApplicationConstant;
import com.manage.library.config.DriverSerialNumber;
import com.manage.library.config.JasptyCryption;
import com.manage.library.config.USBSerialNumber;
import com.manage.library.model.USBModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class USBUtils {

    private static USBModel usbModel;

    static {
        try {
            usbModel = loadUsbModelFromConfig();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Không thể đọc được file cấu hình do đã bị thay đổi.\nVui lòng liên hệ nhà cung cấp!");
        }
    }

    private static USBModel loadUsbModelFromConfig() throws Exception {
        String filePath = ApplicationConstant.PathConfig.APPLICATION_PROPERTIES;
        File file = new File(filePath);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy file cấu hình!");
            System.exit(0);
//            throw new Exception("Cannot find application.properties file!!!!");
        }
        JasptyCryption jc = new JasptyCryption(filePath, "gdvn-serect-key");

//        String decodeVendor = jc.decodeProperty("usb.vendor");
//        String decodeProduct = jc.decodeProperty("usb.product");
        String decodeSerial = jc.decodeProperty("usb.serial");

//        int vendor = Integer.parseInt(decodeVendor, 16);
//        int product = Integer.parseInt(decodeProduct, 16);
        String serial = String.valueOf(decodeSerial);
        return new USBModel(serial);

    }

    public static boolean isUsbKeyConnected() {
        try {
            List<String> listUSB;

            //Cách 1: Lấy USB serial gốc của nhà sản xuất
//            USBSerialNumber localUsb = new USBSerialNumber();
//            listUSB = localUsb.getSerialByCommand();

            // Cách 2: Lấy Driver serial của ổ đĩa
        DriverSerialNumber driverSeri = new DriverSerialNumber();
        listUSB = driverSeri.getVolumeSerialNumbers();
//        listUSB.forEach(action -> System.out.println(action));
// Kiểm tra xem listUSB có chứa usbModel.getSerial() không
            boolean containsSerial = listUSB.contains(usbModel.getSerial());
            if (containsSerial) {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(USBUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;

    }
}
