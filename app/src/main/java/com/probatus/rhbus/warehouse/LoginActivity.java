package com.probatus.rhbus.warehouse;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hanks.htextview.HTextView;
import com.probatus.rhbus.warehouse.Interface.ExceptionHandler;
import com.probatus.rhbus.warehouse.JsonParse.HttpResponse;
import com.probatus.rhbus.warehouse.JsonParse.JsonResponse;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.valdesekamdem.library.mdtoast.MDToast;

import net.igenius.customcheckbox.CustomCheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import lecho.cn.pedant.SweetAlert.SweetAlertDialog;
import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by raksha on 14/3/18.
 */

public class LoginActivity extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SQLitedbHelper mydb;
    EditText eduser,edpass, edCustKey;
    Cursor d=null;
    SearchableSpinner server;

    NoInternetDialog noInternetDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_login);
        MyStatic.setmContext(this);

        edCustKey = (EditText) findViewById(R.id.customerkey);
        eduser = (EditText) findViewById(R.id.username);
        edpass = (EditText) findViewById(R.id.password);
        final CustomCheckBox scb = (CustomCheckBox) findViewById(R.id.scb);
        scb.setChecked(true, true);
        edpass.requestFocus();
        Button btn = (Button) findViewById(R.id.login2);

        mydb = SQLitedbHelper.getInstance(getApplicationContext());
        d = mydb.fetchUSERInfo();
        d.moveToFirst();
        eduser.setText(d.getString(0));
        edCustKey.setText(d.getString(1));

        scb.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if(isChecked) {
                    if (!TextUtils.isEmpty(eduser.getText().toString()) && !TextUtils.isEmpty(edCustKey.getText().toString())) {
                        mydb.updateUSERInfo(eduser.getText().toString(), edCustKey.getText().toString());
                    } else {
                        MDToast.makeText(getApplication(), "Fill the Fields", MDToast.LENGTH_LONG, MDToast.TYPE_WARNING).show();
                    }
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!TextUtils.isEmpty(edpass.getText().toString())) {
                if (!TextUtils.isEmpty(eduser.getText().toString())) {
                    if (!TextUtils.isEmpty(edCustKey.getText().toString())) {
                        if (checkConnection()) {
                            final SweetAlertDialog builder = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Loading");
                            View view= getLayoutInflater().inflate(R.layout.alertbox_progress,null);
                            PieView animatedPie = (PieView) view.findViewById(R.id.pieView);
                            builder.setCancelable(true);
                            builder.setCustomView(view);
                            builder.hideConfirmButton();
                            builder.show();

                            PieAngleAnimation animation = new PieAngleAnimation(animatedPie);
                            animation.setDuration(300);
                            animatedPie.startAnimation(animation);
                            if(scb.isChecked()) {
                                mydb.updateUSERInfo(eduser.getText().toString(), edCustKey.getText().toString());
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    authenticateRequest();

                                    if(builder.isShowing())
                                        builder.dismissWithAnimation();
                                }
                            }, 200);
                        }
                    } else {
                        edCustKey.setError("Customer Key is Empty!!");
                        findViewById(R.id.customerkey).startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.shake));
                    }
                } else {
                    eduser.setError("User Name is Empty!!");
                    findViewById(R.id.username).startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.shake));
                }
            }else {
                edpass.setError("Password is Empty!!");
                findViewById(R.id.password).startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.shake));
            }
            }
        });

        noInternetDialog = new NoInternetDialog.Builder(this).build();

    }

    public void authenticateRequest(){
        ArrayList someVariable = new ArrayList();
        someVariable.add(eduser.getText().toString());
        someVariable.add(edpass.getText().toString());
        someVariable.add(edCustKey.getText().toString());
        MyApplication.getInstance().setSomeVariable(someVariable);
            JsonResponse jsonRe = new JsonResponse();
            ArrayList<String> list = new ArrayList<String>();
            list.add(edCustKey.getText().toString());
            list.add(eduser.getText().toString());
            list.add(edpass.getText().toString());
            edpass.setText("");
            jsonRe.JsonResponse(LoginActivity.this, "Auth3", "inCreds", list, new HttpResponse<JSONObject>() {
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObj = response.getJSONArray("data").getJSONObject(0);
                        MyStatic.setTokenKey(jsonObj.getString("accesstoken"));
                        MyStatic.setProdProcess(jsonObj.getString("RoleID"));
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this, UserActivity.class));
                    } catch (JSONException e) {
                        MDToast.makeText(getApplicationContext(), "Credentials Incorrect, Please Check Again!" + e, MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    }
                }
                public void onFailure(Throwable e, JSONObject response){
                    MDToast.makeText(getApplicationContext(), "Credentials Incorrect, Please Check Again!" + e, MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                }
            });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
