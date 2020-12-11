package com.jackmacc.townadmin.maninfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.jackmacc.townadmin.R;


public class SliderAdpater extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;


    public SliderAdpater(Context context) {
        this.context = context;
    }

    //array
    public int[] slide_images = {
            R.drawable.speakname,
            R.drawable.keyword,
            R.drawable.weighanchor_lite

    };

    public String[] slide_headings = {
            "你的登录名字",
            "登录的密码",
            "满意了点击确认"
    };

    public String[] slide_descs = {

            "英雄不问出处!敢问尊姓大名?",
            "少侠! 闯荡江湖,防人之心不可无阿!",
            "享受生活的人,在冒险中找到了自己"
    };

    @Override
    public int getCount() {
        return slide_images.length;


    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (ConstraintLayout) o;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.side_layout, container, false);

        ImageView slideImageview = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);

        //替换不同的图片,文字资源
        slideImageview.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
