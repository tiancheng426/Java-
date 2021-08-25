package com.example.myapplication3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

public class gameActivity2 extends AppCompatActivity {
    String ip;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ip=getIntent().getStringExtra("ip");
           // setContentView(R.layout.activity_game);

            // 隐藏状态栏，
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //锁定横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            setContentView(new DeskView2(this,ip));//设置view
            //隐藏标题栏
            getSupportActionBar().hide();
    }

}