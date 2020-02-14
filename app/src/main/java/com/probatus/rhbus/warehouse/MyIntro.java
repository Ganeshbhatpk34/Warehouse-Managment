package com.probatus.rhbus.warehouse;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by ganesh on 4/7/18.
 */

public class MyIntro extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(first_fragment);
        addSlide(second_fragment);
        addSlide(third_fragment);
        addSlide(fourth_fragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("WAREHOUSE MANAGEMENT APP", "Best plateform to grow your Bussiness\nProvides Easy and Smooth Way,\nWant To Know How? Here we Go....", R.drawable.splash_production, R.color.level_1));
        addSlide(AppIntroFragment.newInstance("WAREHOUSE DESIGN", "which enables organizations to customize workflow and picking logic\n to make sure that the warehouse is designed for optimized\n inventory allocation", R.drawable.splash_infographic, R.color.level_2));
        addSlide(AppIntroFragment.newInstance("INVENTORY TRACKING", "Do All Production Activity in single Application\n that too in your Hand", R.drawable.splash_planning, R.color.level_3));
        addSlide(AppIntroFragment.newInstance("PICKING PACKING GOODS", "Including Zone,Location picking,\n Warehouse workers can also use lot zoning and\n task interleaving functions to guide the pick-and-pack\n tasks in the most efficient way", R.drawable.user_salesorder, R.color.level_4));
        addSlide(AppIntroFragment.newInstance("DATA SECURITY", "Highly Available, Protected and Secured Data\n with Cloud Based Integrated WMS and ERP System..", R.drawable.splash_report1, R.color.level_0));

        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        try {
            askForPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE}, 1);
        }catch (Exception e){
            Log.e("Intro",""+e);
        }

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
        setSlideOverAnimation();
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        this.startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        this.startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
        showSkipButton(true);
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
        showSkipButton(true);
    }

    @Override
    public void onPause(){
        Bungee.zoom(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
