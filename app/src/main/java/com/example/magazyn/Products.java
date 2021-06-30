package com.example.magazyn;

public class Products {
    public String barcode;
    public String userUid;
    public String productName;
    public String quantity;
    public String location;
    public String userUidBarcode;
    public String userUidProductName;
    public String userUidLocation;
    public String imageUrl;

    public Products(){

    }

    public Products(String barcode, String userUid, String productName, String quantity, String location, String userUidBarcode, String userUidProductName, String userUidLocation, String imageUrl) {
        this.barcode = barcode;
        this.userUid = userUid;
        this.productName = productName;
        this.quantity = quantity;
        this.location = location;
        this.userUidBarcode = userUidBarcode;
        this.userUidProductName = userUidProductName;
        this.userUidLocation = userUidLocation;
        this.imageUrl = imageUrl;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserUidProductName() {
        return userUidProductName;
    }

    public void setUserUidProductName(String userUidProductName) {
        this.userUidProductName = userUidProductName;
    }

    public String getUserUidLocation() {
        return userUidLocation;
    }

    public void setUserUidLocation(String userUidLocation) {
        this.userUidLocation = userUidLocation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
