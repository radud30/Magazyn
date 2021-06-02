package com.example.magazyn;

public class Activity {
    public String activity;
    public String userUid;
    public String date;
    public String whoAddedEmail;

    public Activity(){

    }

    public Activity(String activity, String userUid, String date, String whoAddedEmail) {
        this.activity = activity;
        this.userUid = userUid;
        this.date = date;
        this.whoAddedEmail = whoAddedEmail;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWhoAddedEmail() {
        return whoAddedEmail;
    }

    public void setWhoAddedEmail(String whoAddedEmail) {
        this.whoAddedEmail = whoAddedEmail;
    }
}
