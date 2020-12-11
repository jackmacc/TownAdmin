package com.jackmacc.townadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 新闻扩张内容,图片加标题内容
 */
public class newsex {


    @SerializedName("newsex_id")
    @Expose
    private String newsex_id;

    @SerializedName("newsex_image_name")
    @Expose
    private String newsex_image_name;

    @SerializedName("newsex_content")
    @Expose
    private String newsex_content;

    @SerializedName("newsex_title")
    @Expose
    private String newsex_title;


//    public void setNewsex_id(String newsex_id) {
//        this.newsex_id = newsex_id;
//    }

    public void setNewsex_image_name(String newsex_image_name) {
        this.newsex_image_name = newsex_image_name;
    }

    public void setNewsex_content(String newsex_content) {
        this.newsex_content = newsex_content;
    }

    public void setNewsex_title(String newsex_title) {
        this.newsex_title = newsex_title;
    }

    public String getNewsex_id() {
        return newsex_id;
    }

    public String getNewsex_image_name() {
        return newsex_image_name;
    }

    public String getNewsex_content() {
        return newsex_content;
    }

    public String getNewsex_title() {
        return newsex_title;
    }
}
