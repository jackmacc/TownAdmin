package com.jackmacc.townadmin.Tool;

import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class WindowTool {
    static public void hideWindows(AppCompatActivity AC){
        AC.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    static public void hiddenActionBar(AppCompatActivity AC){
        ActionBar actionBar;
        actionBar=AC.getSupportActionBar();
        actionBar.hide();
    }
}
