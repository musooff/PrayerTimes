package com.simurgh.prayertimes.mosque;

/**
 * Created by moshe on 18/04/2017.
 */

public class DataMosque {
    private String name;
    private String address;
    private Integer image;
    private Integer info;


    public DataMosque(String name, String address, Integer image, Integer info) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getImage() {
        return image;
    }

    public Integer getInfo() {
        return info;
    }
}