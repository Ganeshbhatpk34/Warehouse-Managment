package com.probatus.rhbus.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.probatus.rhbus.warehouse.Interface.INavigationFragment;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.probatus.rhbus.warehouse.fragment.SettingsFragment;
import com.webianks.easy_feedback.EasyFeedback;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by ganapathi on 23/1/20.
 */

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    INavigationFragment currentFragment;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //instantiate the fragmentManager and set the default view to profile
        currentFragment = new SettingsFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame ,(Fragment) currentFragment)
                .commit();

        //initialize the default application settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        Bungee.zoom(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        Bungee.zoom(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            currentFragment = new SettingsFragment();

        } else if(id == R.id.nav_send_feedback){
            new EasyFeedback.Builder(this)
                    .withEmail("rhbussolutions@gmail.com")
                    .withSystemInfo()
                    .build()
                    .start();
        } else if(id == R.id.nav_grcard){

            MyStatic.setWhichactivity("GOODSRECIEPT");
            this.startActivityForResult(new Intent(this, JobViewActivity.class),1);
            finish();
        } else if(id == R.id.nav_pickcard){

            MyStatic.setWhichactivity("PICKORDER");
            this.startActivityForResult(new Intent(this, JobViewActivity.class),1);
            finish();
        } else if(id == R.id.nav_transfer){

            MyStatic.setWhichactivity("TRANSFER");
            this.startActivityForResult(new Intent(this, JobViewActivity.class),1);
        } else if(id == R.id.nav_logout){

            this.startActivityForResult(new Intent(this, LoginActivity.class),1);
            finish();
        }

        openNewFragment(currentFragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openNewFragment(INavigationFragment newFragment){
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, (Fragment) newFragment)
                .commit();
    }
}