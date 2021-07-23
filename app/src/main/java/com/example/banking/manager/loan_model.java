package com.example.banking.manager;

public class loan_model {
    String id,acc,type,amount;

    public String getId() {
        return id;
    }

    public String getAcc() {
        return acc;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public loan_model(String id , String acc, String type, String amount) {
        this.id= id;
        this.acc= acc;
        this.type= type;
        this.amount= amount;
    }
}

