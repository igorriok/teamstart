package com.igorsolonari.teamstart;


public class Participant {

    private String email;
    private int status;

    Participant(){};

    Participant(String email, int status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
