package com.simurgh.prayertimes;

/**
 * Created by moshe on 29/06/2017.
 */

public class DataSuraNames {
    private String name;
    private String transcribed;
    private String translated;
    private int titleNo;
    public DataSuraNames(String name, String transcribed, String translated, int titleNo){
        this.name = name;
        this.transcribed = transcribed;
        this.translated = translated;
        this.titleNo = titleNo;
    }

    public String getName() {
        return name;
    }

    public String getTranscribed() {
        return transcribed;
    }

    public String getTranslated() {
        return translated;
    }

    public int getTitleNo() {
        return titleNo;
    }
}
