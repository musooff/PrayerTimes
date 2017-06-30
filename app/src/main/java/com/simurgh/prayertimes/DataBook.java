package com.simurgh.prayertimes;

import android.content.Intent;

/**
 * Created by moshe on 29/06/2017.
 */

public class DataBook {
    private String name;
    private double size;
    private Integer image;
    private int id;
    private String url;
    private boolean downloaded;
    private String engName;

    public DataBook(String name, String engName,double size, Integer image, int id, boolean downloaded){
        this.name = name;
        this.size = size;
        this.image = image;
        this.id = id;
        this.engName = engName;
        this.downloaded = downloaded;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public double getSize() {
        return size;
    }

    public Integer getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getEngName() {
        return engName;
    }
}
