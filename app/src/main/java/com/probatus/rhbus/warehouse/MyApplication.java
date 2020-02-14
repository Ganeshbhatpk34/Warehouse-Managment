package com.probatus.rhbus.warehouse;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.ArrayList;

/**
 * Created by ganesh on 11/6/18.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private ArrayList someVariable;
    private boolean isFirstTime;
    private String docno,date;

    public ArrayList getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(ArrayList someVariable) {
        this.someVariable = someVariable;
    }

    public boolean getFirsttimeStatus() {
        return isFirstTime;
    }

    public void setFirsttimeStatus(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public String getDocumentNumber(){ return docno;}

    public void setDocumentNumber(String docno){this.docno = docno;}

    public String getDate(){ return date;}

    public void setDate(String date){this.date = date;}





    @Override
    public void onCreate() {

        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
