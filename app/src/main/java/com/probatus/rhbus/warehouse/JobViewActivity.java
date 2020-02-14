package com.probatus.rhbus.warehouse;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.probatus.rhbus.warehouse.Interface.ExceptionHandler;
import com.probatus.rhbus.warehouse.Interface.productversion;
import com.probatus.rhbus.warehouse.JsonParse.JsonResponse;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.probatus.rhbus.warehouse.fragment.WMSPickListFragment;
import com.probatus.rhbus.warehouse.fragment.WMSGRListFragment;
import com.probatus.rhbus.warehouse.fragment.WMSTransferListFragment;
import java.util.ArrayList;
import am.appwise.components.ni.NoInternetDialog;
import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by ganapathi on 24/8/18.
 */

public class JobViewActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    public JsonResponse jsonResponse = new JsonResponse();
    private WMSGRListFragment grListFragment;
    private WMSPickListFragment pickListFragment;
    private WMSTransferListFragment transListFragment;
    MaterialSearchView searchView;
    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyStatic.setmContext(this);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.jobview_activity);

        if(MyStatic.getWhichactivity() == null){
            MyStatic.setWhichactivity("PICKORDER");
        }
        if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
            grListFragment = new WMSGRListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_fragment_containers, grListFragment)
                    .commit();
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
            pickListFragment = new WMSPickListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_fragment_containers, pickListFragment)
                    .commit();
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
            transListFragment = new WMSTransferListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_fragment_containers, transListFragment)
                    .commit();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
                    grListFragment.onQueryTextChange(query);
                } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                    pickListFragment.onQueryTextChange(query);
                } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                    transListFragment.onQueryTextChange(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
                    grListFragment.onQueryTextChange(newText);
                } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                    pickListFragment.onQueryTextChange(newText);
                } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                    transListFragment.onQueryTextChange(newText);
                }
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }
            @Override
            public void onSearchViewClosed() {

            }
        });

        noInternetDialog = new NoInternetDialog.Builder(this).build();

    }


    public void setSearchViewShown(String[] suggestion){
        //searchView.setSuggestions(suggestion);
        /*searchView.showSearch(true);
        searchView.showVoice(true);*/
    }


    public void transferToNextForGR(){
        Intent tender=new Intent(JobViewActivity.this,WMSGRCard.class);
        startActivityForResult(tender,1);
    }

    public void transferToNextForPick(){
        Intent tender=new Intent(JobViewActivity.this,WMSPickCard.class);
        startActivityForResult(tender,1);
    }

    public void transferToNextForTransfer(){
        Intent tender=new Intent(JobViewActivity.this,WMSTransferCard.class);
        startActivityForResult(tender,1);
    }



    public void activityFinish(){
        setResult(1, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
                grListFragment.refreshPlease(true);
            } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
                pickListFragment.refreshPlease(true);
            } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
                transListFragment.refreshPlease(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == MaterialSearchView.REQUEST_VOICE || requestCode == 100 ) && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    Toast.makeText(getApplicationContext(),""+ searchWrd,Toast.LENGTH_SHORT).show();
                    searchView.setQuery(searchWrd, true);
                }
            }
            return;
        } else if(requestCode == RESULT_FIRST_USER && requestCode == RESULT_CANCELED) {
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected)
            showSnack(isConnected);
    }

    public boolean checkConnection() {
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

        /* if(MyStatic.getWhichactivity().equals("GOODSRECIEPT")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_fragment_containers, grListFragment)
                    .commit();
        } else if(MyStatic.getWhichactivity().equals("PICKORDER")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_fragment_containers, pickListFragment)
                    .commit();
        } else if(MyStatic.getWhichactivity().equals("TRANSFER")){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_fragment_containers, transListFragment)
                    .commit();
        } */

        super.onResume();
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
    }
}
