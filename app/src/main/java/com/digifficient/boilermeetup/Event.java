package com.digifficient.boilermeetup;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.internal.id;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by taylor on 12/6/14.
 */
public class Event{
    private int eventId;
    private String name;
    private LatLng position;
    private String location;
    private String description;
    private String startTime;
    private String endTime;
    private int numAttendees;

    /*
    public Event(int id, LatLng position, String location, String description, String startTime, int numAttendees){
        this.eventId = id;
        this.position = position;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.numAttendees = numAttendees;
    }
    */

    public int getEventId(){
        return this.eventId;
    }

    public String getName(){
        return this.name;
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

    public String getEndTime(){return this.endTime; }

    public int getNumAttendees(){
        return this.numAttendees;
    }


    public void setEventId(int id){
       this.eventId = id;
    }

    public void setName(String name){
        this.name = name;
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

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public void setNumAttendees(int numAttendees){
       this.numAttendees = numAttendees;
    }


}


