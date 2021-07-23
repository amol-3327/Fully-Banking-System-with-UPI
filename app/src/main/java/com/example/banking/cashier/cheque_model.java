package com.example.banking.cashier;

public class cheque_model {
    String id,acc,pages,status,date;

    public String getId() {
        return id;
    }

    public String getAcc() {
        return acc;
    }

    public String getPages() {
        return pages;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public cheque_model(String id , String acc, String pages, String status, String date) {
        this.id= id;
        this.acc= acc;
        this.pages= pages;
        this.status= status;
        this.date = date;
    }
}

