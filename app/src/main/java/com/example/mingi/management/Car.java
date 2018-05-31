package com.example.mingi.management;

/**
 * Created by MINGI on 2018-05-10.
 */
// adfdfdfdfdfd
public class Car {

    String id;
    String  carNum;
    String  startPlace;
    String endPlace;
    String startTime;
    String  startDay;
    String  endTime;
    String endDay;
    String kilometer;
    int no;

    public Car(String startPlace, String endPlace, String startTime , String endTime , String startDay, String endDay, String kilometer, String carNum, int no){

        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDay = startDay;
        this.endDay = endDay;
        this.kilometer = kilometer;

        this.carNum = carNum;
        this.no = no;

    }


    public Car(String id, String carNum, String startPlace, String endPlace, String startTime, String startDay, String endTime, String endDay, String kilometer, int no) {
        this.id = id;
        this.carNum = carNum;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.startTime = startTime;
        this.startDay = startDay;
        this.endTime = endTime;
        this.endDay = endDay;
        this.kilometer = kilometer;
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }


    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }
}
