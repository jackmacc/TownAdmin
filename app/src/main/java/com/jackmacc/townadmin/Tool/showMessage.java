package com.jackmacc.townadmin.Tool;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class showMessage {


    //==================================
    //不能直接调用 Toast
    //==================================
    static public void Toast_Looper (String toastString , Context context) {

        final   String toastStringMsg=toastString;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                try {
                    Toast.makeText(context, toastStringMsg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
    }
    //==================================
}
