package com.igorsolonari.teamstart;


import java.util.ArrayList;
import java.util.Date;

public class Event {

    private String name;
    private ArrayList<Participant> participants;
    private Date date;

    public Event(){}

    public Event(ArrayList<Participant> participants, Date date) {
        this.name = name;
        this.participants = participants;
        this.date = date;
    }

    public void addParticipants(ArrayList<Participant> participants){
        this.participants = participants;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
