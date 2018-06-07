package com.example.mingi.management.DrivingJoin;

/**
 * Created by MINGI on 2018-05-14.
 */

public class Listitem {
    private String name;
    private String upperaddr, midaddr, roadName, roadNo;
    private String lat;
    private String lon;

    public Listitem() {}

    public Listitem(String name, String upperaddr, String midaddr, String roadName, String roadNo, String lat, String lon) {
        this.name = name;
        this.upperaddr = upperaddr;
        this.midaddr = midaddr;
        this.roadName = roadName;
        this.roadNo = roadNo;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getFullAddr() {
        return upperaddr + " " + midaddr + " " + roadName + roadNo;
    }

    public String getUpperaddr() {
        return upperaddr;
    }

    public String getMidaddr() {
        return midaddr;
    }

    public String getRoadName() {
        return roadName;
    }

    public String getRoadNo() {
        return roadNo;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}

