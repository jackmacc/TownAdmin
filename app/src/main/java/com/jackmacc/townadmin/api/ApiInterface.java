package com.jackmacc.townadmin.api;


import com.jackmacc.townadmin.models.News;
import com.jackmacc.townadmin.models.returnback;
import com.jackmacc.townadmin.models.townNews;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;


//修改接口的时候一定要激活服务器端接口

public interface ApiInterface {

    @Multipart
    @POST("Android_News_add")  //这里是自己post文件的地址
    Observable<townNews> postGoodsReturnPostEntitys(

            @PartMap Map<String, RequestBody> map,  //第一个参数使用注解@PartMap用于多参数的情况，如果是单个参数也可使用注解@Part。
            //类型Map<String, RequestBody>中，Map第一个泛型String是服务器接收用于文件上传参数字段的Key
            // 第二个泛型RequestBody是OkHttp3包装的上传参数字段的Value

            @Part List<MultipartBody.Part> parts
            //第二个参数使用注解@Part用于文件上传，
            // 多文件上传使用集合类型List<MultipartBody.Part>，
            // 单文件可以使用类型MultipartBody.Part
    );

    // @Multipart这是Retrofit专门用于文件上传的注解，需要配合@POST一起使用。

    //定义克隆接口
   @Multipart
    @POST("Android_News_add_swiper")  //这里是自己post文件的地址
    Observable<townNews> postGoodsReturnPostEntitys_swiper(
            @PartMap Map<String, RequestBody> map,
            @Part List<MultipartBody.Part> parts
    );

//定义克隆接口
//定义克隆接口
@Multipart
@POST("Android_News_add_newsex")  //这里是自己post文件的地址
Observable<townNews> postGoodsReturnPostEntitys_newsex(
        @PartMap Map<String, RequestBody> map,
        @Part List<MultipartBody.Part> parts
);








    @Multipart
    @POST("Android_News_edit")
    Observable<returnback>postTownNews_edit(
            //在 node.js 提取  req.query.id
            //   @Query("apiKey")  String apiKey
            @PartMap Map<String, RequestBody> map,
            @Part List<MultipartBody.Part> parts
    );


    //定义克隆接口
    @Multipart
    @POST("Android_News_edit_swiper")
    Observable<returnback>postTownNews_edit_swiper(
            //在 node.js 提取  req.query.id
            //   @Query("apiKey")  String apiKey
            @PartMap Map<String, RequestBody> map,
            @Part List<MultipartBody.Part> parts
    );


    //无图片更新数据
    @Multipart
    @POST("Android_News_edit_noimg")
    Observable<returnback>postTownNews_edit_noimg(
            //在 node.js 提取  req.query.id
            //   @Query("apiKey")  String apiKey
            @PartMap Map<String, RequestBody> map
    );

    //定义克隆接口
    @Multipart
    @POST("Android_News_edit_noimg_swiper")
    Observable<returnback>postTownNews_edit_noimg_swiper(
            //在 node.js 提取  req.query.id
            //   @Query("apiKey")  String apiKey
            @PartMap Map<String, RequestBody> map
    );



    //读取content 长文
    @GET("Android_News_read_content")
    Call<returnback>getTownNews_content(

            //定义请求参数
            @Query("news_id") String news_id //需要修改
            //   @Query("apiKey")  String apiKey
    );

    //定义克隆接口
    @GET("Android_News_read_content_swiper")
    Call<returnback>getTownNews_content_swiper(

            //定义请求参数
            @Query("news_id") String news_id //需要修改
            //   @Query("apiKey")  String apiKey
    );

    //读取新闻列表  按照分类
    @GET("Android_News_list")
    Call<townNews>getTownNews(

            @Query("newsType_id") String newsType_id //需要修改

    );


    //Android_News_list_newsex
 //读取新闻扩展
    @GET("Android_News_list_newsex")
    Call<townNews>getTownNews_newsex(

            @Query("news_id") String news_id //需要修改

    );




     //读取新闻类型列表
     @GET("Android_Newstype_list")
     Call<townNews>getTownNewstype(



    );



    //定义克隆接口 无分类
    @GET("Android_News_list_swiper")
    Call<townNews>getTownNews_swiper(

           // @Query("town") String town ,//需要修改
            @Query("swiper") String swiper //需要修改

    );



    //删除一条新闻
    @GET("Android_News_delete")
    Call<returnback>getTownNews_delete(
                    //定义请求参数
                    @Query("id") String id //需要修改
                    //在 node.js 提取  req.query.id
                    //   @Query("apiKey")  String apiKey
   );

    //定义克隆接口
    @GET("Android_News_delete_swiper")
    Call<returnback>getTownNews_delete_swiper(
            //定义请求参数
            @Query("id") String id //需要修改
            //在 node.js 提取  req.query.id
            //   @Query("apiKey")  String apiKey
    );















    @GET("Android_News_read")
    Call<returnback>getTownNews_read(

            //定义请求参数
            @Query("id") String id //需要修改
            //在 node.js 提取  req.query.id
            //   @Query("apiKey")  String apiKey
    );

    //定义数据接口  ,请求的  get
    @GET("top-headlines")
    Call<News> getNews(

            //定义请求参数
            @Query("country") String country //需要修改
            //@Query("apiKey")String apiKey


    );

    //get

    @GET("everything")
    Call<News> getNewsSearch(

            //参数列表
            @Query("q")String keyword,  //查询关键字
            @Query("Language")String language // 语言字段
          //  @Query("sorBy")String sortBy     //排序字段
          //  @Query("apiKey")String apiKey    //查询的验证 key




    );

    //获取 news 的扩展新闻,
    //读取content 长文
    @GET("Android_News_read_newsex")
    Call<returnback>getTownNews_read_newsex(

            //定义请求参数
            @Query("news_id") String news_id //需要修改
            //   @Query("apiKey")  String apiKey
    );


}
