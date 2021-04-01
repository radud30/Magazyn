package com.example.magazyn;

public class Pracownik {
    String email;
    String creator_uid;
    boolean isworker;

    public Pracownik(){

    }

    public Pracownik(String email, String creator_uid,boolean isworker) {
        this.email = email;
        this.creator_uid = creator_uid;
        this.isworker = isworker;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreator_uid() {
        return creator_uid;
    }

    public void setCreator_uid(String creator_uid) {
        this.creator_uid = creator_uid;
    }

    public boolean isIsworker() {
        return isworker;
    }

    public void setIsworker(boolean isworker) {
        this.isworker = isworker;
    }
}
