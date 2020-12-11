package com.jackmacc.townadmin;

public  class DataUrl {

    //登录验证  php
    public String checkoutPath="town_checkout_php.php"; //服务器 登录会话 S_id 销毁
    public String loginPath="town_login_php.php";
    public String regPath_lite="PHP_register_lite.php";

    //登录验证 node.js

    public String js_checkoutPath="town_logout";
    public String js_loginPath="town_login";
    public String js_regPath_lite="town_register_lite";

    //主服务器url
    private static String Hostpath="http://192.168.10.196:8070";
    private static String HostNewspath="http://192.168.10.196:8071";

    private static String HostIP="192.168.10.196";

    public DataUrl(String hostpath, String hostNewspath) {
        this.Hostpath = hostpath;
        this.HostNewspath = hostNewspath;
    }


    //新闻系列
    private final String deleteNews_id  ="/Android_News_delete"; //删除
    private final String addNews_id     ="/Android_News_add"; //添加
    private final String eidtNews_id    ="/Android_News_edit"; //编辑
    private final String readNews_list  ="/Android_News_list"; //新闻列表
    private final String readNews_id    ="/Android_News_read"; //读一条新闻

    //新闻图片位置
    public final String storeNewsImgPath="public/images";



    //产品系列
    public final String deleteProduct_id  ="towndemo/Android_to_product_delete.php";
    public final String readProduct_data  ="town_civ_id"; //读取产品图片信息 php 接口
  //  public final String readProduct_data  ="towndemo/Android_to_product_Read_id.php"; //读取产品图片信息 php 接口
    public final String writeProduct_data ="towndemo/Android_to_product_Add_Edit_imgfile_upload.php";//写入产品图片信息 php 接口
    public final String ReadProduct_List  ="towndemo/Android_to_product_Read_list.php"; //读取产品列表

    //产品图片的存放地址
    public final String storeProductImgPath="towndemo/product_images";



    public DataUrl() {
    }

    public String getHostNewspath() {
        return HostNewspath;
    }

    public static void setHostNewspath(String hostNewspath) {
        HostNewspath = hostNewspath;
    }

    public DataUrl(String hostpath) { //建设的时候就修路径
        Hostpath = hostpath;
    }

    public String getHostpath() {
        return Hostpath;
    }



    public static void setHostpath(String hostpath) {
        Hostpath = hostpath;
    }


    public static void setHostpathAll(String newhostIP) {


        Hostpath ="http://"+newhostIP+":8070";
        HostNewspath="http://"+newhostIP+":8071";
        HostIP=newhostIP;
    }

    public static String getHostIP(){

        return HostIP;
    }
}
