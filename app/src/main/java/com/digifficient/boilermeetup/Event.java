package com.digifficient.boilermeetup;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by taylor on 12/6/14.
 */
public class Event {
    private String id;
    private LatLng position;
    private String location;
    private String description;
    private String startTime;
    private int numAttendees;

    public String getEventId(){
        return this.id;
    }

    public LatLng getPositiion(){
        return this.position;
    }

    public String getLocation(){
        return this.location;
    }

    public String getDesription(){
        return this.description;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public int getNumAttendees(){
        return this.numAttendees;
    }


    public void setEventId(String id){
        this.id = id;
    }

    public void setPosition(LatLng position){
        this.position = position;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setDescription(String description){
       this.description = description;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void setNumAttendees(int numAttendees){
       this.numAttendees = numAttendees;
    }


}


