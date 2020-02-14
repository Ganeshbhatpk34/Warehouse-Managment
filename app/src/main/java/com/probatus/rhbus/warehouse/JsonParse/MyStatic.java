package com.probatus.rhbus.warehouse.JsonParse;

import android.content.Context;
import android.database.Cursor;

import com.probatus.rhbus.warehouse.Interface.productversion;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzbgp.NULL;

/**
 * Created by ganapathi on 25/8/18.
 */

public class MyStatic {
    private static Context mContext;
    private static Integer position;
    private static String customer=NULL,location=NULL,whichactivity=null,pietype=null,token=null;
    private static Cursor arrayCursor=null;
    private static ArrayList<productversion> Constproductversion = new ArrayList<productversion>();

    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        MyStatic.mContext = mContext;
    }

    public static Integer getTabPosition() {
        return position;
    }

    public static void setTabPosition(Integer mContext) {
        MyStatic.position = mContext;
    }

    public static String getProdProcess() {
        return customer;
    }

    public static void setProdProcess(String mContext) {
        MyStatic.customer = mContext;
    }

    public static String getLineNumber() {
        return location;
    }

    public static void setLineNumber(String mContext) {
        MyStatic.location = mContext;
    }

    public static String getWhichactivity() {
        return whichactivity;
    }

    public static void setWhichactivity(String mContext) {
        MyStatic.whichactivity = mContext;
    }

    public static String getPietype() {
        return pietype;
    }

    public static void setPietype(String mContext) {
        MyStatic.pietype = mContext;
    }

    public static Cursor getCursorData() {
        return arrayCursor;
    }

    public static void setCursorData(Cursor mContext) {
        MyStatic.arrayCursor = mContext;
    }

    public static String getTokenKey() {
        return token;
    }

    public static void setTokenKey(String token) {
        MyStatic.token = token;
    }

    public static ArrayList<productversion> getProducts() {
        return Constproductversion;
    }

    public static void setProducts(ArrayList<productversion> token) {
        MyStatic.Constproductversion = token;
    }
}