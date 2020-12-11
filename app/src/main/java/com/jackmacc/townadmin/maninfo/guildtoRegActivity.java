package com.jackmacc.townadmin.maninfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.jackmacc.townadmin.R;


public class guildtoRegActivity extends AppCompatActivity {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;
            TextView Viewhint;

    private Button mNextBtn,mBackBtn,btn_ok;
    EditText guild_name,guild_passwod;


    private  SliderAdpater sliderAdpater;

    private int mCurrentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guildtoreg);


        mSlideViewPager=findViewById(R.id.sLideViewPager);
        mDotLayout=findViewById(R.id.dotslayout);
        mNextBtn=findViewById(R.id.nextBtn);
        mBackBtn=findViewById(R.id.prevBtn);
        Viewhint=findViewById(R.id.Viewhint);
        Viewhint.setVisibility(View.GONE);

        btn_ok=findViewById(R.id.btn_ok);

        guild_name=findViewById(R.id.guild_name);

        guild_passwod=findViewById(R.id.guild_password);
        guild_passwod.setVisibility(View.GONE);
        btn_ok.setVisibility(View.GONE);
        mBackBtn.setVisibility(View.GONE);

        sliderAdpater=new SliderAdpater(this);
        mSlideViewPager.setAdapter(sliderAdpater);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
                switch (mCurrentPage){

                    case 0:
                        mNextBtn.setVisibility(View.VISIBLE);
                        guild_name.setVisibility(View.VISIBLE);
                        guild_passwod.setVisibility(View.GONE);
                        btn_ok.setVisibility(View.GONE);
                        break;
                    case 1:
                        guild_name.setVisibility(View.GONE);
                        guild_passwod.setVisibility(View.VISIBLE);
                        guild_passwod.requestFocus();
                        guild_passwod.setSelection(0);
                        guild_passwod.setFocusableInTouchMode(true);
                        mNextBtn.setVisibility(View.VISIBLE);
                        btn_ok.setVisibility(View.GONE);
                        break;
                    case 2:

                        guild_passwod.setVisibility(View.GONE);
                        if (String.valueOf(guild_passwod.getText()).equals("")||String.valueOf(guild_name.getText()).equals("")){
                            btn_ok.setVisibility(View.GONE);

                            Viewhint.setVisibility(View.VISIBLE);
                            Viewhint.setText("请填写完整");

                            // Toast_Looper("显示:"+String.valueOf(guild_passwod.getText()),getApplicationContext());
                        }
                        else {
                            Viewhint.setVisibility(View.GONE);
                            btn_ok.setVisibility(View.VISIBLE);
                        }
// startActivity(new Intent(guildtoRegActivity.this,HomeActivity.class));
                        break;

                    case 3:


                        break;
                }

            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                mSlideViewPager.setCurrentItem(mCurrentPage + 1);

                switch (mCurrentPage){


                case 1:
                        guild_name.setVisibility(View.GONE);
                    guild_passwod.setVisibility(View.VISIBLE);
                    guild_passwod.requestFocus();
                    guild_passwod.setSelection(0);
                    guild_passwod.setFocusableInTouchMode(true);
                    btn_ok.setVisibility(View.GONE);
                        break;
                case 2:

                    guild_passwod.setVisibility(View.GONE);
                    if (String.valueOf(guild_passwod.getText()).equals("")||String.valueOf(guild_name.getText()).equals("")){
                        btn_ok.setVisibility(View.GONE);

                        Viewhint.setVisibility(View.VISIBLE);
                        Viewhint.setText("请填写完整");

                        // Toast_Looper("显示:"+String.valueOf(guild_passwod.getText()),getApplicationContext());
                    }
                    else {
                        Viewhint.setVisibility(View.GONE);
                        btn_ok.setVisibility(View.VISIBLE);
                    }
// startActivity(new Intent(guildtoRegActivity.this,HomeActivity.class));
                break;

                 }



            }

        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent=new Intent();
                regIntent.setClass(guildtoRegActivity.this,RegisterManActivity.class);
                regIntent.putExtra("name",guild_name.getText().toString());
                regIntent.putExtra("pass",guild_passwod.getText().toString());

                startActivity(regIntent);



            }
        });

    }

    public void addDotsIndicator(int position){
        mDotLayout.removeAllViews();
        mDots=new TextView[3];
        for(int i=0;i<mDots.length;i++){
            mDots [i]=new TextView(this);
            mDots [i].setText(Html.fromHtml("&#8226"));

            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mDots[i].setTextSize(35);
            mDotLayout.addView(mDots[i]);

        }


        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));

        }
    }

   ViewPager.OnPageChangeListener viewListener= new ViewPager.OnPageChangeListener() {
       @Override
       public void onPageScrolled(int i, float v, int i1) {

       }

       @Override
       public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage=i;
            if(i==0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mNextBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.GONE);

                guild_name.setVisibility(View.VISIBLE);
                guild_passwod.setVisibility(View.GONE);
                btn_ok.setVisibility(View.GONE);
                mNextBtn.setText("下一页");
                mBackBtn.setText("");

                Viewhint.setVisibility(View.GONE);

            }else if(i==mDots.length-1){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                mNextBtn.setVisibility(View.GONE);

                guild_passwod.setVisibility(View.GONE);

                if (String.valueOf(guild_passwod.getText()).equals("")||String.valueOf(guild_name.getText()).equals("")){
                    btn_ok.setVisibility(View.GONE);

                    Viewhint.setVisibility(View.VISIBLE);
                    Viewhint.setText("请填写完整");

                   // Toast_Looper("显示:"+String.valueOf(guild_passwod.getText()),getApplicationContext());
                }
                else

                     btn_ok.setVisibility(View.VISIBLE);






                mBackBtn.setText("返回");
            }else{
                btn_ok.setVisibility(View.GONE);
                guild_passwod.setVisibility(View.VISIBLE);

                guild_passwod.requestFocus();
                guild_passwod.setSelection(0);
                guild_passwod.setFocusableInTouchMode(true);
                guild_name.setVisibility(View.GONE);
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mNextBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);
                mNextBtn.setText("下一页");
                mBackBtn.setText("返回");
                Viewhint.setVisibility(View.GONE);
            }
       }

       @Override
       public void onPageScrollStateChanged(int i) {

       }
   };
}
