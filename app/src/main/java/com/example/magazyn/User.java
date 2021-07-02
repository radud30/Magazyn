package com.example.magazyn;

public class User {
    public String name;
    public String age;
    public String email;
    public String userUid;
    public String mapImage;

    public User(){

    }

    public User(String name, String age, String email, String userUid, String mapImage) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.userUid = userUid;
        this.mapImage = mapImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getMapImage() {
        return mapImage;
    }

    public void setMapImage(String mapImage) {
        this.mapImage = mapImage;
    }
}
