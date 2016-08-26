package com.ginsmile.pocketweather.model;

/**
 * Created by xujin on 15/12/13.
 */
public class City {
    private int id;
    private String code_id;
    private String county;
    private String city;
    private String province;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
