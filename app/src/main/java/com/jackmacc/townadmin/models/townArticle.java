package com.jackmacc.townadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class townArticle {

    @SerializedName("source")
    @Expose
    private Source source; //这是一个双重对象.
    //作者,或者归属


    @SerializedName("news_images_path")
    @Expose
    private String news_images_path;




    @SerializedName("news_title")
    @Expose
    private String news_title;

    @SerializedName("news_date")
    @Expose
    private String news_date;

    @SerializedName("news_desc")
    @Expose
    private String news_desc;

    @SerializedName("news_content")
    @Expose
    private String news_content;

    @SerializedName("news_url")
    @Expose
    private String news_url;


    @SerializedName("news_id")
    @Expose
    private String news_id;


    public String getNews_content() {
        return news_content;
    }

    public void setNews_content(String news_content) {
        this.news_content = news_content;
    }

    public String getNews_images_path() {
        return news_images_path;
    }

    public void setNews_images_path(String news_images_path) {
        this.news_images_path = news_images_path;
    }

    public String getNews_url() {
        return news_url;
    }

    public void setNews_url(String news_url) {
        this.news_url = news_url;
    }


    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_date() {
        return news_date;
    }

    public void setNews_date(String news_date) {
        this.news_date = news_date;
    }

    public String getNews_desc() {
        return news_desc;
    }

    public void setNews_desc(String news_desc) {
        this.news_desc = news_desc;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }
}
