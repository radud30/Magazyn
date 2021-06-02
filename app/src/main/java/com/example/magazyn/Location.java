package com.example.magazyn;

public class Location {
    public String userUid;
    public String location;
    public String userUidLocation;

    public Location(){

    }

    public Location(String userUid, String location, String userUidLocation) {
        this.userUid = userUid;
        this.location = location;
        this.userUidLocation = userUidLocation;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserUidLocation() {
        return userUidLocation;
    }

    public void setUserUidLocation(String userUidLocation) {
        this.userUidLocation = userUidLocation;
    }
}
