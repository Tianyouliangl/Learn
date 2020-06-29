package com.location.com;

import com.amap.api.services.core.LatLonPoint;

import java.io.Serializable;

public class PoiltemBean implements Serializable {
    private String title;
    private String address;
    private int distance;
    private String searchAddress;
    private String cityCode;
    private Boolean isChecked;
    private LatLonPoint latLonPoint;
    private String mapPng;

    PoiltemBean(String cityCode,String searchAddress,String title,String address,int distance,LatLonPoint latLonPoint,Boolean isChecked){
        setCityCode(cityCode);
        setSearchAddress(searchAddress);
        setTitle(title);
        setAddress(address);
        setDistance(distance);
        setLatLonPoint(latLonPoint);
        setChecked(isChecked);
    }

    public String getMapPng() {
        return mapPng;
    }

    public void setMapPng(String mapPng) {
        this.mapPng = mapPng;
    }

    public LatLonPoint getLatLonPoint() {
        return latLonPoint;
    }

    public void setLatLonPoint(LatLonPoint latLonPoint) {
        this.latLonPoint = latLonPoint;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }


    public String getSearchAddress() {
        return searchAddress;
    }

    public void setSearchAddress(String searchAddress) {
        this.searchAddress = searchAddress;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
