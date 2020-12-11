package com.jackmacc.townadmin.maninfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jackmacc.townadmin.home_page;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences mSharedPreferences;//获取
    public SharedPreferences.Editor mEditor;//存放
    public Context mContext;
    int PRIVATE_MODE=0;

    private static final  String PREF_NAME="LOGIN";
    private static final  String LOGIN="IS_LOGIN";

    public static final String NAME="NAME";
    public static final String EMAIL="EMAIL";

    public static final String ID="ID";
    public static final String SESSION_ID="SESSION_ID";

    public SessionManager(Context context) {
        this.mContext = context;
        mSharedPreferences =
                context.getSharedPreferences(PREF_NAME,PRIVATE_MODE); //获取

        mEditor=mSharedPreferences.edit(); //得到
    }

    public void createSessiond(String name,String Email,String id){ //向 SP editor 定义充值
        mEditor.putBoolean(LOGIN,true);
        mEditor.putString(NAME,name);
        mEditor.putString(EMAIL,Email);
        mEditor.putString(ID,id);
        mEditor.apply();


    }

    public void addSessionId(String sessionid){
        mEditor.putString(SESSION_ID,sessionid);
        mEditor.apply();

    }



    public boolean isLoggin(){ //查询是否登录过
        return mSharedPreferences.getBoolean(LOGIN,false);

    }

    public void checkLogin(){
        if(!this.isLoggin()){//效验登录
            Intent i=new Intent(mContext, maninfoLoginActivity.class);
            mContext.startActivity(i);
           // ((PHP_HomeActivity)mContext).finish();

        }
    }

    public HashMap<String,String> getUserDetail(){
        HashMap<String,String> user=new HashMap<>();//一个存放 属性的实体
        user.put(NAME,mSharedPreferences.getString(NAME,null));
        user.put(EMAIL,mSharedPreferences.getString(EMAIL,null));
        user.put(ID,mSharedPreferences.getString(ID,null));
        user.put(SESSION_ID,mSharedPreferences.getString(SESSION_ID,null));
        return user;

    }

    public void logout(){
        mEditor.clear();//清除登录
        mEditor.commit();
        Intent i=new Intent(mContext, home_page.class);
        mContext.startActivity(i);
       // ((object)mContext).finish();

    }

    public void EditorClear() {
        mEditor.clear();//清除登录
        mEditor.commit();
    }
}
