package com.example.mingi.management.DrivingJoin;

public class FromToInfo {
    int distance, totTime;

    FromToInfo (int d, int t) {
        distance = d;
        totTime = t;
    }

    FromToInfo() {}

    void gotTotTime(int s) {
       totTime = s / 60;
    }
}
