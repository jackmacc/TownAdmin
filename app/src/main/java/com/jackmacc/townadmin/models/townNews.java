package com.jackmacc.townadmin.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class townNews {

    @SerializedName("status") //返回状态
    @Expose
    private String status;

    @SerializedName("totalResult") //数量
    @Expose
    private int totalResult;

    @SerializedName("articles")  //返回的新闻列表
    @Expose
    private List<townArticle> townarticles;

    @SerializedName("newsexs")  //返回的新闻列表
    @Expose
    private List<newsex> newsexs;

    public List<newsex> getNewsexs() {
        return newsexs;
    }

    @SerializedName("townnewstype")  //返回的新闻列表
    @Expose
    private List <townnewstype> townnewstype;

    public List<townArticle> getArticles() {
        return townarticles;
    }


    public List<townnewstype> gettownnewstype() {
        return townnewstype;
    }
}
