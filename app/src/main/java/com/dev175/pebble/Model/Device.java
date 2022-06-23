package com.dev175.pebble.Model;

import android.widget.ImageView;

public class Device {

    private ImageView caseImg;
    private String name;
    private String address;
    private boolean state;

    public Device(ImageView caseImg, String name, String address) {
        this.caseImg = caseImg;
        this.name = name;
        this.address = address;
    }

    public Device() {
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public ImageView getCaseImg() {
        return caseImg;
    }

    public void setCaseImg(ImageView caseImg) {
        this.caseImg = caseImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
