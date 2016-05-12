package com.brioal.linecode.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.brioal.linecode.MainActivity;
import com.brioal.linecode.R;
import com.brioal.linecode.util.Constancts;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;

public class LauncherActivity extends Activity {


    @Bind(R.id.launcher_image)
    ImageView launcherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
        Bmob.initialize(this, Constancts.APPID);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.launcher_zoom);
        launcherImage.setAnimation(animation);
        animation.setFillAfter(true);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                LauncherActivity.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
