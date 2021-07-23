package com.example.banking.manager;

public class loan_finish_model {
    String id,acc,date,amount;

    public String getId() {
        return id;
    }

    public String getAcc() {
        return acc;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public loan_finish_model(String id , String acc, String date, String amount) {
        this.id= id;
        this.acc= acc;
        this.date= date;
        this.amount= amount;
    }
}

