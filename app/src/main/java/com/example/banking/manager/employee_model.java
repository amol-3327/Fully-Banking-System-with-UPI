package com.example.banking.manager;

public class employee_model {
    String id,name,post;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPost() {
        return post;
    }


    public employee_model(String id , String name, String post) {
        this.id= id;
        this.name= name;
        this.post= post;
    }
}

