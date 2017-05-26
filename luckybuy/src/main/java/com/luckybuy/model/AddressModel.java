package com.luckybuy.model;

import com.luckybuy.SettingActivity;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/7/17.
 */
public class AddressModel implements Serializable {

    private String firstname;

    private String city;

    private String district;

    private String address;

    private String mobile;

    private boolean isdefault;

    private long addressidx;

    private long uidx;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }

    public long getAddressidx() {
        return addressidx;
    }

    public void setAddressidx(long addressidx) {
        this.addressidx = addressidx;
    }

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "firstname='" + firstname + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", isdefault=" + isdefault +
                ", addressidx=" + addressidx +
                ", uidx=" + uidx +
                '}';
    }
}
