package com.jackmacc.townadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class newsContent {

    @SerializedName("news_content") //返回状态
    @Expose
    private  String news_content;

    public String getNews_content() {
        return news_content;
    }
}
