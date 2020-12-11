package com.jackmacc.townadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class townnewstype {

    @SerializedName("newstype_id")
    @Expose
    int newstype_id;

    @SerializedName("newstype_name")
    @Expose
    String newstype_name;

    @SerializedName("newstype_desc")
    @Expose
    String newstype_desc;

    @SerializedName("newstype_image")
    @Expose
    String newstype_image;


    public int getNewstype_id() {
        return newstype_id;
    }

    public String getNewstype_name() {
        return newstype_name;
    }

    public String getNewstype_desc() {
        return newstype_desc;
    }

    public String getNewstype_image() {
        return newstype_image;
    }
}
