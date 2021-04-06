package com.example.magazyn;

public class Workers {
    String email;
    String creatorUid;
    String worker;

    public Workers(){

    }

    public Workers(String email, String creatorUid, String worker) {
        this.email = email;
        this.creatorUid = creatorUid;
        this.worker = worker;
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
}
