package com.simurgh.prayertimes.names;

/**
 * Created by moshe on 29/06/2017.
 */

public class DataNames {
    private String nameArabic;
    private String nameArabicTranscripted;
    private String nameEng;
    private int id;
    public DataNames(String nameArabic, String nameArabicTranscripted, String nameEng, int id){
        this.nameArabic = nameArabic;
        this.nameArabicTranscripted = nameArabicTranscripted;
        this.nameEng = nameEng;
        this.id = id;
    }

    public String getNameArabic() {
        return nameArabic;
    }

    public String getNameArabicTranscripted() {
        return nameArabicTranscripted;
    }

    public String getNameEng() {
        return nameEng;
    }

    public int getId() {
        return id;
    }
}
