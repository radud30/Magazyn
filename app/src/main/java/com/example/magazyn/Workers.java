package com.example.magazyn;

public class Workers {
    public String email;
    public String creatorUid;
    public String worker;
    public String permissionAdd;
    public String permissionStockStatus;
    public String permissionCollect;
    public String permissionLocation;

    public Workers(){

    }

    public Workers(String email, String creatorUid, String worker, String permissionAdd, String permissionStockStatus, String permissionCollect, String permissionLocation) {
        this.email = email;
        this.creatorUid = creatorUid;
        this.worker = worker;
        this.permissionAdd = permissionAdd;
        this.permissionStockStatus = permissionStockStatus;
        this.permissionCollect = permissionCollect;
        this.permissionLocation = permissionLocation;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getPermissionAdd() {
        return permissionAdd;
    }

    public void setPermissionAdd(String permissionAdd) {
        this.permissionAdd = permissionAdd;
    }

    public String getPermissionStockStatus() {
        return permissionStockStatus;
    }

    public void setPermissionStockStatus(String permissionStockStatus) {
        this.permissionStockStatus = permissionStockStatus;
    }

    public String getPermissionCollect() {
        return permissionCollect;
    }

    public void setPermissionCollect(String permissionCollect) {
        this.permissionCollect = permissionCollect;
    }

    public String getPermissionLocation() {
        return permissionLocation;
    }

    public void setPermissionLocation(String permissionLocation) {
        this.permissionLocation = permissionLocation;
    }
}
