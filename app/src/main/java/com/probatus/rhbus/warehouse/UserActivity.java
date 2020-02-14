package com.probatus.rhbus.warehouse;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import com.probatus.rhbus.warehouse.Adapter.UserAdapter;
import com.probatus.rhbus.warehouse.Interface.ItemClickListener;
import com.probatus.rhbus.warehouse.JsonParse.HttpResponse;
import com.probatus.rhbus.warehouse.JsonParse.JsonResponse;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.webianks.easy_feedback.EasyFeedback;
import org.json.JSONObject;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import lecho.cn.pedant.SweetAlert.SweetAlertDialog;
import spencerstudios.com.bungeelib.Bungee;

import static java.lang.Boolean.TRUE;

/**
 * Created by ganapathi on 24/8/18.
 */

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        ItemClickListener {

    LayoutAnimationController animation;
    FloatingActionButton fab;
    SwipeRefreshLayout refreshCollection;
    RecyclerView listView;

    public JsonResponse jsonResponse = new JsonResponse();
    ArrayList<String> sampleListView = new ArrayList<String>();
    ArrayList<String> list = new ArrayList<String>();
    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        listView = (RecyclerView) findViewById(R.id.sampleListView);
        refreshCollection =(SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshCollection.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPlease();
            }
        });

        setUserView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        noInternetDialog = new NoInternetDialog.Builder(this).build();
    }

    @Override
    public void onClick(final View view, int position) {
        if(position == 0){
            MyStatic.setWhichactivity("GOODSRECIEPT");
            this.startActivityForResult(new Intent(UserActivity.this, JobViewActivity.class),1);
        } else if(position == 1){
            MyStatic.setWhichactivity("PICKORDER");
            this.startActivityForResult(new Intent(UserActivity.this, JobViewActivity.class),1);
        } else if(position == 3){
            MyStatic.setWhichactivity("TRANSFER");
            this.startActivityForResult(new Intent(UserActivity.this, JobViewActivity.class),1);
        } else if(position == 4){
            MyStatic.setWhichactivity("SETTING");
            this.startActivityForResult(new Intent(UserActivity.this, NavigationActivity.class),1);
        }
    }


    public void setUserView(){
        list = new ArrayList<String>();
        list.add(MyStatic.getTokenKey());
        if(checkConnection()) {
            this.jsonResponse.JsonResponse(this, "wmsgrList", "getAllGR", list, new HttpResponse<JSONObject>() {
                public void onResponse(final JSONObject grheader) {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(MyStatic.getTokenKey());
                    UserActivity.this.jsonResponse.JsonResponse(UserActivity.this, "wmsPickList", "getAllPick", list,
                            new HttpResponse<JSONObject>() {
                                public void onResponse(final JSONObject pickheader) {
                                    UserActivity.this.jsonResponse.JsonResponse(UserActivity.this, "wmsTrasnferList", "getAllRecords", UserActivity.this.list,
                                            new HttpResponse<JSONObject>() {
                                                public void onResponse(final JSONObject transheader) {
                                                    sampleListView = new ArrayList<String>();
                                                    try {
                                                        sampleListView.add("Goods Receipt ( " + grheader.getJSONArray("result").length() + " )");
                                                        sampleListView.add("Pick Order ( " + pickheader.getJSONArray("result").length() + " )");
                                                        sampleListView.add("Movements ( 0 )");
                                                        sampleListView.add("Transfer ( " + transheader.getJSONArray("result").length() + " )");
                                                        sampleListView.add("Setting");
                                                    } catch (Exception e) {
                                                        sampleListView.add("Goods Receipt ( 0 )");
                                                        sampleListView.add("Pick Order ( 0 )");
                                                        sampleListView.add("Movements ( 0 )");
                                                        sampleListView.add("Transfer ( 0 )");
                                                        sampleListView.add("Setting");
                                                        MDToast.makeText(UserActivity.this, "" + e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                                                    }
                                                    UserAdapter adapter = new UserAdapter(sampleListView);
                                                    listView.setHasFixedSize(true);
                                                    listView.setAdapter(adapter);
                                                    LinearLayoutManager llm = new LinearLayoutManager(UserActivity.this);
                                                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                                                    listView.setLayoutManager(llm);
                                                    adapter.setClickListener(UserActivity.this);
                                                }
                                            });
                                }
                            });
                }
            });
        }
    }


    public void refreshPlease(){
        refreshCollection.post(new Runnable() {
            @Override
            public void run() {
                refreshCollection.setRefreshing(true);
            }
        });
        listView.setLayoutAnimation(animation);
        sampleListView.clear();
        setUserView();
        refreshCollection.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshCollection.setRefreshing(false);
            }
        },200);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            onFinishAcivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_maintab, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                onFinishAcivity();
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
            MyStatic.setWhichactivity("SETTING");
            this.startActivityForResult(new Intent(this, NavigationActivity.class),1);
        } else if(id == R.id.nav_send_feedback){
            new EasyFeedback.Builder(this)
                    .withEmail("rhbussolutions@gmail.com")
                    .withSystemInfo()
                    .build()
                    .start();
        } else if(id == R.id.nav_grcard){

            MyStatic.setWhichactivity("GOODSRECIEPT");
            this.startActivityForResult(new Intent(this, JobViewActivity.class),1);
        } else if(id == R.id.nav_pickcard){

            MyStatic.setWhichactivity("PICKORDER");
            this.startActivityForResult(new Intent(this, JobViewActivity.class),1);
        } else if(id == R.id.nav_transfer){

            MyStatic.setWhichactivity("TRANSFER");
            this.startActivityForResult(new Intent(this, JobViewActivity.class),1);
        } else if(id == R.id.nav_logout){

            onFinishAcivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFinishAcivity(){
        final SweetAlertDialog builder = new SweetAlertDialog(UserActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Are you sure ?")
                .setCustomImage(R.drawable.alertexit)
                .setContentText("Are you sure want to Logout");
        builder.setConfirmText("YES Exit!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        finish();
                    }
                });
        builder.setCancelText("CANCEL")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
        builder.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected)
            showSnack(isConnected);
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            showSnack(isConnected);
            if (noInternetDialog != null ? !noInternetDialog.isShowing() : false) {
                noInternetDialog.show();
            }
        }
        return isConnected;
    }

    @Override
    public void onPause() {
        Bungee.zoom(this);
        MyApplication.getInstance().setConnectivityListener(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        Bungee.zoom(this);
        MyApplication.getInstance().setConnectivityListener(this);

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(noInternetDialog != null ? noInternetDialog.isShowing() : false)
            noInternetDialog.onDestroy();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connection Established!";
            color = Color.GREEN;
        } else {
            message = "Connection couldn't Established!\nCheck the Internet Connection";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        sbView.setBackgroundColor(color);
        snackbar.show();
    }
}
