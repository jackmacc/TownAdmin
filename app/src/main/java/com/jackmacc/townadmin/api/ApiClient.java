package com.jackmacc.townadmin.api;

import com.jackmacc.townadmin.DataUrl;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//读取接口
public class ApiClient {

    private static DataUrl dataUrl=new DataUrl();
    //这里是求取的网页.
    public static  String BASE_URL=""; //这里多 " / " 是必须的.


    public static Retrofit retrofit; //生成Retrofit 工具对象


    public static Retrofit getApiClient(){
        dataUrl=new DataUrl();
        BASE_URL=dataUrl.getHostNewspath();
        if(retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(BASE_URL) //打包了 读取的 Url
                  //.client(getUnsafeOkHttpClient().build())
                  .addConverterFactory(GsonConverterFactory.create())//retrofit 包中的一个工具,
                   .build();
        }


        return retrofit;
    }


//    public static ApiInterface apiInterface;
//
//    public static ApiInterface getRetrofitInterface(String baseUrl) {
//        dataUrl=new DataUrl();
//        BASE_URL=dataUrl.getHostNewspath();
//        if (apiInterface == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
////                .addConverterFactory(ScalarsConverterFactory.create()) //string
//                    //   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .build();
//
//            apiInterface=retrofit.create(ApiInterface.class);
//
//        }
//        return apiInterface;
//    }

    //被getApiClinet 调用 (暂时不使用)
    public static OkHttpClient.Builder getUnsafeOkHttpClient(){

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[]
                                getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();


            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
