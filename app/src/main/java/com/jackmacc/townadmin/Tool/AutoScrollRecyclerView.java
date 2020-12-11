package com.jackmacc.townadmin.Tool;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public
class AutoScrollRecyclerView  extends RecyclerView {

    public AutoScrollRecyclerView(Context context) {
        super(context);
    }

    public AutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    //    // 拦截事件；
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        // 拦截触摸事件；
//        return true;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        // 消费事件；
//        return true;
//    }
}
