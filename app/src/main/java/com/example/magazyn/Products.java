package com.example.magazyn;

public class Products {
    public String barcode;
    public String userUid;
    public String productName;
    public String quantity;
    public String userUidBarcode;

    public Products(){

    }

    public Products(String barcode, String userUid, String productName, String quantity, String userUidBarcode) {
        this.barcode = barcode;
        this.userUid = userUid;
        this.productName = productName;
        this.quantity = quantity;
        this.userUidBarcode = userUidBarcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUserUidBarcode() {
        return userUidBarcode;
    }

    public void setUserUidBarcode(String userUidBarcode) {
        this.userUidBarcode = userUidBarcode;
    }

}
