package com.example.magazyn;

public class Produkty {
    public String kod;
    public String userId;
    public String produktNazwa;
    public String ilosc;
    public String kod_userid;

    public Produkty(){

    }

    public Produkty(String kod, String userId, String produktNazwa, String ilosc, String kod_userid) {
        this.kod = kod;
        this.userId = userId;
        this.produktNazwa = produktNazwa;
        this.ilosc = ilosc;
        this.kod_userid = kod_userid;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProduktNazwa() {
        return produktNazwa;
    }

    public void setProduktNazwa(String produktNazwa) {
        this.produktNazwa = produktNazwa;
    }

    public String getIlosc() {
        return ilosc;
    }

    public void setIlosc(String ilosc) {
        this.ilosc = ilosc;
    }

    public String getKod_userid() {
        return kod_userid;
    }

    public void setKod_userid(String kod_userid) {
        this.kod_userid = kod_userid;
    }

}
