package com.probatus.rhbus.warehouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.probatus.rhbus.warehouse.Interface.ExceptionHandler;
import me.wangyuwei.particleview.ParticleView;
import spencerstudios.com.bungeelib.Bungee;


public class StartActivity extends AppCompatActivity {
    SQLitedbHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.splash_screen);

        ParticleView mParticleView=(ParticleView) findViewById(R.id.mParticleView);
        mParticleView.startAnim();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myDb = SQLitedbHelper.getInstance(StartActivity.this);
                boolean exists = myDb.checkDatabase();
                myDb.close();
                if (exists) {
                    MyApplication.getInstance().setFirsttimeStatus(false);
                    StartActivity.this.startActivity(new Intent(StartActivity.this, LoginActivity.class));
                } else {
                    MyApplication.getInstance().setFirsttimeStatus(true);
                    StartActivity.this.startActivity(new Intent(StartActivity.this, MyIntro.class));
                }
            }
        }, 3000);

    }

    @Override
    public void onPause(){
        super.onPause();
        Bungee.zoom(this);
        finish();
    }
}
