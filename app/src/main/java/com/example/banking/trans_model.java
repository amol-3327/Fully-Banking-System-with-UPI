package com.example.banking;

public class trans_model {
    String id,acc,send,rece,date,status,amount,rmk;

    public String getId() {
        return id;
    }

    public String getAcc() {
        return acc;
    }

    public String getSend() {
        return send;
    }

    public String getRece() {
        return rece;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }

    public String getRmk() {
        return rmk;
    }

    public trans_model(String id , String acc, String send, String rece, String date, String status, String amount,String rmk) {
        this.id= id;
        this.acc= acc;
        this.send= send;
        this.rece= rece;
        this.date = date;
        this.status = status;
        this.amount = amount;
        this.rmk = rmk;
    }

}

