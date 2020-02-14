package com.probatus.rhbus.warehouse.Interface;

import android.view.View;
import android.widget.TextView;

/**
 * Created by ganesh on 7/4/18.
 */

public interface AdapterCallback {
    void onMethodCallback(String ls, String qnty, TextView ttl, View v, String status, int j);
}
