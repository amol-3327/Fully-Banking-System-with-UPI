package com.example.banking.manager;

public class loan_types_model {
    String id,type,rate;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRate() {
        return rate;
    }


    public loan_types_model(String id , String type, String rate) {
        this.id= id;
        this.type= type;
        this.rate= rate;
    }
}

