package com.example.mapsapp;

public class TaksiData {
    private String username;
    private String lat;
    private String lon;
    private String plate_num;
    private String station_name;

    public TaksiData(String username, String lat, String lon, String plate_num, String station_name) {
        this.username = username;
        this.lat = lat;
        this.lon = lon;
        this.plate_num = plate_num;
        this.station_name = station_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }
}
