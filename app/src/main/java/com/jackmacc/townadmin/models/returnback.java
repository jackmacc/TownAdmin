package com.jackmacc.townadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public
class returnback {


    @SerializedName("newsContent") //返回状态
    @Expose
    private List<newsContent>   newsContent;

    public List<com.jackmacc.townadmin.models.newsContent> getNewsContent() {
        return newsContent;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @SerializedName("id") //返回状态
    @Expose
    private String id;


    @SerializedName("type") //返回状态
    @Expose
    private String type;


    @SerializedName("newsex_to_news") //返回状态
    @Expose
    private List <newsex> newsex_s;

    public List<newsex> getNewsex_s() {
        return newsex_s;
    }
}
