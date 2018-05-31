package com.example.mingi.management;

/**
 * Created by MINGI on 2018-05-10.
 */

// good girl
public class BC {

    String ids;
    String id;
    String BC_name;
    String  BC_level;
    String  BC_com;
    String BC_phone;
    String BC_mail;
    String  BC_add;
    String  BC_lat;
    String BC_lon;
    String BC_photo;
    int no;

    public BC(String BC_name, String BC_level, String BC_com, String BC_phone, String BC_mail, String BC_add, String BC_lat, String BC_lon, String BC_photo, int no) {
        this.BC_name = BC_name;
        this.BC_level = BC_level;
        this.BC_com = BC_com;
        this.BC_phone = BC_phone;
        this.BC_mail = BC_mail;
        this.BC_add = BC_add;
        this.BC_lat = BC_lat;
        this.BC_lon = BC_lon;
        this.BC_photo = BC_photo;
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBC_name() {
        return BC_name;
    }

    public void setBC_name(String BC_name) {
        this.BC_name = BC_name;
    }

    public String getBC_level() {
        return BC_level;
    }

    public void setBC_level(String BC_level) {
        this.BC_level = BC_level;
    }

    public String getBC_com() {
        return BC_com;
    }

    public void setBC_com(String BC_com) {
        this.BC_com = BC_com;
    }

    public String getBC_phone() {
        return BC_phone;
    }

    public void setBC_phone(String BC_phone) {
        this.BC_phone = BC_phone;
    }

    public String getBC_mail() {
        return BC_mail;
    }

    public void setBC_mail(String BC_mail) {
        this.BC_mail = BC_mail;
    }

    public String getBC_add() {
        return BC_add;
    }

    public void setBC_add(String BC_add) {
        this.BC_add = BC_add;
    }

    public String getBC_lat() {
        return BC_lat;
    }

    public void setBC_lat(String BC_lat) {
        this.BC_lat = BC_lat;
    }

    public String getBC_lon() {
        return BC_lon;
    }

    public void setBC_lon(String BC_lon) {
        this.BC_lon = BC_lon;
    }

    public String getBC_photo() {
        return BC_photo;
    }

    public void setBC_photo(String BC_photo) {
        this.BC_photo = BC_photo;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
