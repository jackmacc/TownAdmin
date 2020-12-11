package com.jackmacc.townadmin.testpack5;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.jackmacc.townadmin.R;

public class testpack_nesscroll extends AppCompatActivity  implements AppBarLayout.OnOffsetChangedListener {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testpack_activity_nesscroll);


//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setTitle("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;


    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

//        if (percentage == 1f && isHideTolbarView) {
//            date_behavior.setVisibility(View.GONE);
//            titleAppbar.setVisibility(View.VISIBLE);
//            isHideTolbarView = !isHideTolbarView;
//
//
//        } else if (percentage < 1f && isHideTolbarView) {
//            date_behavior.setVisibility(View.VISIBLE);
//            titleAppbar.setVisibility(View.GONE);
//            isHideTolbarView = !isHideTolbarView;
//
//
//        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}