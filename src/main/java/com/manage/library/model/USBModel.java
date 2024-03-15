package com.manage.library.model;

public class USBModel {

    private short productId;
    private short vendorId;
    private String serial;

    public USBModel() {
    }

    public USBModel(String serial) {
        this.serial = serial;
    }

    
    public USBModel(short productId, short vendorId, String serial) {
        this.productId = productId;
        this.vendorId = vendorId;
        this.serial = serial;
    }
    

    public short getProductId() {
        return productId;
    }

    public void setProductId(short productId) {
        this.productId = productId;
    }

    public short getVendorId() {
        return vendorId;
    }

    public void setVendorId(short vendorId) {
        this.vendorId = vendorId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
    

}
