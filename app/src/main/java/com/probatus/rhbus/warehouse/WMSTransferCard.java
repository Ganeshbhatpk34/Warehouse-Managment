package com.probatus.rhbus.warehouse;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.htextview.HTextView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.probatus.rhbus.warehouse.Adapter.ListAdapter;
import com.probatus.rhbus.warehouse.Interface.AdapterCallback;
import com.probatus.rhbus.warehouse.Interface.ExceptionHandler;
import com.probatus.rhbus.warehouse.Interface.productversion;
import com.probatus.rhbus.warehouse.JsonParse.HttpResponse;
import com.probatus.rhbus.warehouse.JsonParse.JsonResponse;
import com.probatus.rhbus.warehouse.JsonParse.MyStatic;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import am.appwise.components.ni.NoInternetDialog;
import lecho.cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by ganapathi on 29/10/18.
 */

public class WMSTransferCard extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener, AdapterCallback {

    JsonResponse jsonResponse = new JsonResponse();
    RecyclerView lv;
    SwipeRefreshLayout refreshSelected;
    RecyclerViewFastScroller recyclerViewFastScroller;
    RecyclerView.SmoothScroller smoothScroller;
    FancyButton list_void;
    LayoutAnimationController animation;
    ListAdapter arrayAdapter;
    private TextView tv_stat;
    JSONArray dataSourcePICKADHQuantityToPick = new JSONArray();
    JSONArray dataHeader = new JSONArray();
    JSONArray dataLines = new JSONArray();

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    TextView textInfo, txtDocument,txtCustomer,txtCustVendLabel;
    HTextView textStatus;
    ListView listViewPairedDevice;
    LinearLayout inputPane;
    FancyButton btnSend, btnCancel, alertPopup;
    Dialog dialog;
    Integer indexOfList = null;
    Boolean isAddMore = false;
    String msgReceived = "";
    MaterialSearchView searchView;

    public productversion productversionsList;
    public static ArrayList<productversion> productversions= new ArrayList<>();
    public ArrayList<productversion> selected_items = new ArrayList<>();
    ToneGenerator toneGen1;
    NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyStatic.setmContext(this);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_listcontent);
        toneGen1 = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);

        refreshSelected =(SwipeRefreshLayout) findViewById(R.id.listswipeRefreshLayout);
        lv = (RecyclerView) findViewById(R.id.list_view);
        recyclerViewFastScroller = (RecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        list_void= (FancyButton) findViewById(R.id.list_void);
        txtDocument = (TextView) findViewById(R.id.txtDocument);
        txtCustomer = (TextView) findViewById(R.id.txtCustomer);
        txtCustVendLabel = (TextView) findViewById(R.id.txtCustVendlabel);
        txtCustVendLabel.setText("Location");
        txtDocument.setText(MyApplication.getInstance().getDocumentNumber());

        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        if(checkConnection()) {
            getHeaderDetails();
        }

        refreshSelected.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                indexOfList = null;
                setLocalListView();
                lv.setLayoutAnimation(animation);
                onItemsLoadComplete(refreshSelected);
            }
        });

        list_void.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_items.size() > 0){
                    ArrayList<String> arrlist = new ArrayList<String>();
                    for (int j = 0; j < selected_items.size(); j++) {
                        switch (checkData(j)) {
                            case 1: {
                                arrlist.add(selected_items.get(j).getProduct_itemcode()+ ", Qty:ZERO");
                                break;
                            }
                            case 2: {
                                arrlist.add(selected_items.get(j).getProduct_itemcode()+ ", Qty:EXCEEDS");
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                    String list_receipt[] = new String[arrlist.size()];
                    int index = 0;
                    for (String number : arrlist) {
                        list_receipt[index] = number;
                        index ++;
                    }
                    if(arrlist.size() == 0){
                        list_receipt = new String[1];
                        list_receipt[0] = "All Item are Sent!";
                    }
                    final SweetAlertDialog builder = new SweetAlertDialog(WMSTransferCard.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Confirmation")
                            .setCustomImage(R.drawable.alertcart);
                    builder.setCancelable(false);
                    View dialogView = WMSTransferCard.this.getLayoutInflater().inflate(R.layout.summary_popup, null);
                    ListView susp= dialogView.findViewById(R.id.simpleListView);
                    ArrayAdapter<String> itemsusp=new ArrayAdapter<String>(WMSTransferCard.this,android.R.layout.simple_list_item_1,android.R.id.text1,list_receipt);
                    susp.setAdapter(itemsusp);
                    builder.setCustomView(dialogView);
                    builder.setConfirmText("Yes, Proceed")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sendBulkRequest();
                                    sDialog.dismissWithAnimation();
                                }
                            });
                    builder.setCancelText("No, Wait")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            });
                    builder.show();
                }else
                    MDToast.makeText(WMSTransferCard.this, "No Lines are Present!", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            }
        });
        dialog = new Dialog(WMSTransferCard.this);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setTitle("Bluetooth Setup");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_scalereader);
        textInfo = (TextView) dialog.findViewById(R.id.info);
        textStatus = (HTextView) dialog.findViewById(R.id.status);
        listViewPairedDevice = (ListView) dialog.findViewById(R.id.pairedlist);

        inputPane = (LinearLayout) dialog.findViewById(R.id.inputpane);
        btnSend = (FancyButton) dialog.findViewById(R.id.send);
        btnCancel = (FancyButton) dialog.findViewById(R.id.cancel);
        alertPopup = (FancyButton) dialog.findViewById(R.id.alertPopup);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Double inputQty = Double.parseDouble(msgReceived);
                    JSONObject jsonObject = dataSourcePICKADHQuantityToPick.getJSONObject(indexOfList);
                    if (isAddMore) {
                        inputQty = inputQty + jsonObject.getDouble("ReQuantitySent");
                    }
                    if (jsonObject.getDouble("QuantitySent") < inputQty) {
                        msgReceived = jsonObject.getString("QuantitySent");
                        inputQty = Double.parseDouble(msgReceived);
                        MDToast.makeText(getApplicationContext(), "Quantity is Greater than Stock Avalable!, Quantity Cannot be Exceeded!!", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                    }
                    jsonObject.remove("ReQuantitySent");
                    jsonObject.put("ReQuantitySent", inputQty.toString());

                    msgReceived = Double.toString(roundTwoDecimals(jsonObject.getDouble("QuantitySent") - jsonObject.getDouble("QuantityReceived") - inputQty));
                    inputQty = Double.parseDouble(msgReceived);

                    jsonObject.remove("ReCalculatedQty");
                    jsonObject.put("ReCalculatedQty", inputQty.toString());
                    if (dataSourcePICKADHQuantityToPick.length() > indexOfList) {
                        dataSourcePICKADHQuantityToPick.put(indexOfList, jsonObject);
                    } else dataSourcePICKADHQuantityToPick.put(jsonObject);
                    setLocalListView();
                } catch(NumberFormatException e) {
                    MDToast.makeText(getApplicationContext(),"Not a Valid Number!",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                }
                if(dialog.isShowing())
                    dialog.dismiss();
                resetBluetoothConn(true);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing())
                    dialog.dismiss();
                resetBluetoothConn(true);
            }
        });
        alertPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing())
                    dialog.dismiss();
                applyQuantity(indexOfList);
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            MDToast.makeText(getApplicationContext(),
                    "FEATURE_BLUETOOTH NOT SUPPORT",
                    MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
        if (bluetoothAdapter == null) {
            MDToast.makeText(getApplicationContext(),
                    "Bluetooth is not supported on this hardware platform",
                    MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
            finish();
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(arrayAdapter != null)
                    arrayAdapter.filter(query,1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(arrayAdapter != null)
                    arrayAdapter.filter(newText,1);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        noInternetDialog = new NoInternetDialog.Builder(this).build();
    }

    public void sendBulkRequest(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(MyStatic.getTokenKey());
        list.add(dataSourcePICKADHQuantityToPick.toString());
        jsonResponse.JsonResponse(WMSTransferCard.this, "wmsTrasnferAdv_android", "itemEditEndHandler", list, new HttpResponse<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    String array = response.getString("status");
                    if(array.equals("DONE")){
                        MDToast.makeText(getApplicationContext(), "Successfully Received the Items", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                        MyStatic.setWhichactivity("TRANSFER");
                        Intent tender=new Intent(WMSTransferCard.this,JobViewActivity.class);
                        startActivity(tender);
                        finish();
                    } else {
                        setLocalListView();
                        lv.setLayoutAnimation(animation);
                        onItemsLoadComplete(refreshSelected);
                        MDToast.makeText(WMSTransferCard.this, "Failed to Received the Items: "+ array, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                    }
                } catch (Exception e) {
                    MDToast.makeText(WMSTransferCard.this, response + ""+ e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });
    }

    public void setBletooth() {
        String stInfo = bluetoothAdapter.getName() + " " + bluetoothAdapter.getAddress();
        if(textInfo != null)
            textInfo.setText(stInfo);

        dialog.show();
        if(myThreadConnectBTdevice != null ? myThreadConnectBTdevice.bluetoothSocket != null ? myThreadConnected != null : false : false){
            if(myThreadConnectBTdevice.bluetoothSocket.isConnected()){
                msgReceived = "0.00";
                textStatus.animateText("");
                textInfo.setText(myThreadConnectBTdevice.bluetoothDevice.getName());
                myThreadConnected.displayValue = "Establishing Connection..";
                listViewPairedDevice.setVisibility(View.GONE);
                inputPane.setVisibility(View.VISIBLE);
                textStatus.animateText(msgReceived);
                dialog.setCancelable(false);
                if(isAddMore){
                    alertPopup.setVisibility(View.VISIBLE);
                } else {
                    alertPopup.setVisibility(View.GONE);
                }
            } else {
                setup();
                textStatus.reset("");
                msgReceived = "Establishing Connection..";
                listViewPairedDevice.setVisibility(View.VISIBLE);
                inputPane.setVisibility(View.GONE);
                textStatus.animateText(msgReceived);
                if(isAddMore){
                    alertPopup.setVisibility(View.VISIBLE);
                } else {
                    alertPopup.setVisibility(View.GONE);
                }
            }
        } else {
            setup();
            textStatus.reset("");
            msgReceived = "Establishing Connection..";
            listViewPairedDevice.setVisibility(View.VISIBLE);
            inputPane.setVisibility(View.GONE);
            textStatus.animateText(msgReceived);
            if(isAddMore){
                alertPopup.setVisibility(View.VISIBLE);
            } else {
                alertPopup.setVisibility(View.GONE);
            }
        }
    }

    private void onItemsLoadComplete(SwipeRefreshLayout rf) {
        rf.setRefreshing(false);
    }

    public String isNullChecker(String userEmail){
        if(userEmail != null && (!TextUtils.equals(userEmail ,"null")) && (!TextUtils.isEmpty(userEmail))){
            return userEmail;
        } else {
            return "-";
        }
    }

    private void getHeaderDetails(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(MyStatic.getTokenKey());
        list.add(MyApplication.getInstance().getDocumentNumber());
        jsonResponse.JsonResponse(this, "wmsTrasnferAdv", "openTransHeader", list, new HttpResponse<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("result");
                    dataHeader = array;
                    txtDocument.setText(MyApplication.getInstance().getDocumentNumber());
                    String loc = isNullChecker(array.getJSONObject(0).getString("FromStorageUnitCode"))
                            + " -> "+ isNullChecker(array.getJSONObject(0).getString("ToStorageUnitCode"));
                    txtCustomer.setText(loc);
                    if(dataHeader.length() > 0){
                        setListforView();
                    }
                } catch (Exception e) {
                    MDToast.makeText(getApplicationContext(), ""+ e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });
    }

    public void OrderList(String Orderby){
        if(Orderby.equals("ItemCode")) {
            Collections.sort(productversions, productversion.StuNameComparator);
            arrayAdapter.notifyDataSetChanged();
        } else {
            Collections.sort(productversions, productversion.StuDescComparator);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void setting(){
        ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < productversions.size(); i++) {
            String name = productversions.get(i).getAndroid_version_name();
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word.toLowerCase(Locale.getDefault())) &&
                    !strAlphabets.contains(word.toUpperCase(Locale.getDefault()))) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }
        recyclerViewFastScroller.setRecyclerView(lv);
        recyclerViewFastScroller.setUpAlphabet(mAlphabetItems);
    }

    private void setListforView(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(MyStatic.getTokenKey());
        list.add(MyApplication.getInstance().getDocumentNumber());
        jsonResponse.JsonResponse(this, "wmsTrasnferAdv_android", "getAlltranferformapletree", list, new HttpResponse<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    selected_items.clear();
                    JSONArray array = response.getJSONArray("result");
                    if(dataSourcePICKADHQuantityToPick.length() == 0){
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            jsonObject.put("ReCalculatedQty",jsonObject.getString("QuantitySent"));
                            jsonObject.put("ReQuantitySent","0.00");
                            dataSourcePICKADHQuantityToPick.put(jsonObject);
                        }
                    }
                    dataLines = dataSourcePICKADHQuantityToPick;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject c = array.getJSONObject(i);
                        productversionsList = new productversion();
                        productversionsList.setAndroid_version_name(c.getString("Description"));
                        productversionsList.setProduct_itemcode(c.getString("ItemCode"));
                        productversionsList.setProduct_description(c.getString("Description"));
                        for (int j = 0; j < dataSourcePICKADHQuantityToPick.length(); j++) {
                            JSONObject currObject = dataSourcePICKADHQuantityToPick.getJSONObject(j);
                            if(currObject.getString("LineNo").equals(c.getString("LineNo"))) {
                                productversionsList.setProduct_quantity(currObject.getString("ReQuantitySent"));
                                productversionsList.setProduct_delete(currObject.getString("ReCalculatedQty"));
                                break;
                            }
                        }
                        productversionsList.setProduct_total(c.getString("LotNo"));
                        productversionsList.setProduct_lineno(c.getString("LineNo"));
                        productversionsList.setProduct_linedisc(c.getString("QuantitySent"));
                        productversionsList.setProduct_brand(c.getString("QuantityReceived"));
                        productversionsList.setProduct_barcode(c.getString("QuantitySent"));
                        productversionsList.setProduct_category(c.getString("VariantCode"));
                        productversionsList.setProduct_hsn(c.getString("BaseUOM"));
                        productversionsList.setProduct_uom(c.getString("ItemCode") + " "+
                                " " + c.getString("Description") + " "+ c.getString("Barcode"));
                        selected_items.add(productversionsList);
                    }
                    productversions = selected_items;
                    LinearLayoutManager listSelected=new LinearLayoutManager(WMSTransferCard.this);
                    lv.setLayoutManager(listSelected);
                    lv.setHasFixedSize(false);
                    arrayAdapter = new ListAdapter(WMSTransferCard.this, selected_items);
                    lv.setAdapter(arrayAdapter);
                    if(indexOfList != null){
                        smoothScroller.setTargetPosition(indexOfList);
                        listSelected.startSmoothScroll(smoothScroller);
                    }
                    setting();
                    arrayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    MDToast.makeText(WMSTransferCard.this, "" + e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                }
            }
        });
    }

    public void setLocalListView(){
        try {
            selected_items.clear();
            for (int i = 0; i < dataSourcePICKADHQuantityToPick.length(); i++) {
                JSONObject c = dataSourcePICKADHQuantityToPick.getJSONObject(i);
                productversionsList = new productversion();
                productversionsList.setAndroid_version_name(c.getString("Description"));
                productversionsList.setProduct_itemcode(c.getString("ItemCode"));
                productversionsList.setProduct_description(c.getString("Description"));
                productversionsList.setProduct_quantity(c.getString("ReQuantitySent"));
                productversionsList.setProduct_delete(c.getString("ReCalculatedQty"));
                productversionsList.setProduct_total(c.getString("LotNo"));
                productversionsList.setProduct_lineno(c.getString("LineNo"));
                productversionsList.setProduct_linedisc(c.getString("QuantitySent"));
                productversionsList.setProduct_brand(c.getString("QuantityReceived"));
                productversionsList.setProduct_barcode(c.getString("QuantitySent"));
                productversionsList.setProduct_category(c.getString("VariantCode"));
                productversionsList.setProduct_hsn(c.getString("BaseUOM"));
                productversionsList.setProduct_uom(c.getString("ItemCode") + " " +
                        " " + c.getString("Description") + " " + c.getString("Barcode"));
                selected_items.add(productversionsList);
            }
            LinearLayoutManager listSelected = new LinearLayoutManager(WMSTransferCard.this);
            lv.setLayoutManager(listSelected);
            lv.setHasFixedSize(false);
            arrayAdapter = new ListAdapter(WMSTransferCard.this, selected_items);
            lv.setAdapter(arrayAdapter);
            if(indexOfList != null){
                smoothScroller.setTargetPosition(indexOfList);
                listSelected.startSmoothScroll(smoothScroller);
            }
            setting();
            arrayAdapter.notifyDataSetChanged();
        } catch (Exception e){}
    }

    public int checkData(int previous) {
        try {
            Double receivedQty = Double.valueOf(selected_items.get(previous).getProduct_brand());
            Double ordQty = Double.valueOf(selected_items.get(previous).getProduct_barcode());
            Double val = Double.valueOf(selected_items.get(previous).getProduct_quantity());
            if (val < 0 || (val == 0)) {
                // reset the entered input
                return 1;
            }
            if ((val + receivedQty) > ordQty) {
                return 2;
            }
            return 3;
        } catch (Exception e){
            return 1;
        }
    }

    public void onMethodCallback(final String ls, final String qnty, final TextView ttl, View v, String status, final int j) {
        if(status == "qt"){
            isAddMore = false;
            try{
                for (int i = 0; i < dataSourcePICKADHQuantityToPick.length(); i++) {
                    JSONObject currObject = dataSourcePICKADHQuantityToPick.getJSONObject(i);
                    if(currObject.getString("LineNo").equals(ls)) {
                        indexOfList = i;
                        applyQuantity(i);
                        break;
                    }
                }
            }catch (Exception e){}
        } else if(status == "weight"){
            isAddMore = false;
            try{
                for (int i = 0; i < dataSourcePICKADHQuantityToPick.length(); i++) {
                    JSONObject currObject = dataSourcePICKADHQuantityToPick.getJSONObject(i);
                    if(currObject.getString("LineNo").equals(ls)) {
                        indexOfList = i;
                        break;
                    }
                }
            }catch (Exception e){}

            setBletooth();
        } else if(status == "addmore"){
            isAddMore = true;
            try{
                for (int i = 0; i < dataSourcePICKADHQuantityToPick.length(); i++) {
                    JSONObject currObject = dataSourcePICKADHQuantityToPick.getJSONObject(i);
                    if(currObject.getString("LineNo").equals(ls)) {
                        indexOfList = i;
                        setBletooth();
                        break;
                    }
                }
            }catch (Exception e){}
        } else if(status == "redo"){
            isAddMore = false;
            final SweetAlertDialog builder = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Are you Sure ?")
                    .setCustomImage(R.drawable.alertvoid)
                    .setContentText("Are you sure want to Clear ?")
                    .setConfirmText("YES! CLEAR")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            try{
                                for (int i = 0; i < dataSourcePICKADHQuantityToPick.length(); i++) {
                                    JSONObject currObject = dataSourcePICKADHQuantityToPick.getJSONObject(i);
                                    if(currObject.getString("LineNo").equals(ls)) {
                                        indexOfList = i;
                                        clearAlltheWeights(i);
                                        break;
                                    }
                                }
                            } catch (Exception e){}
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
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public void applyQuantity(final int j){
        final SweetAlertDialog builder = new SweetAlertDialog(WMSTransferCard.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Quantity")
                .setCustomImage(R.drawable.alertquantity)
                .setContentText("Enter the Quantity :");
        final EditText input = new EditText(WMSTransferCard.this);
        input.setHint("Enter The Quantity");
        input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setCustomView(input);
        builder.setConfirmText("Apply")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        try{
                            InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
                        }catch (Exception e){}
                        if (!TextUtils.isEmpty(input.getText().toString())
                                && !input.getText().toString().equals("0")) {
                            try {
                                Double inputQty = Double.parseDouble(input.getText().toString());
                                JSONObject jsonObject = dataSourcePICKADHQuantityToPick.getJSONObject(j);
                                if (isAddMore) {
                                    inputQty = inputQty + jsonObject.getDouble("ReQuantitySent");
                                }
                                if (jsonObject.getDouble("QuantitySent") < inputQty) {
                                    msgReceived = jsonObject.getString("QuantitySent");
                                    inputQty = Double.parseDouble(msgReceived);
                                    MDToast.makeText(getApplicationContext(), "Quantity is Greater than Stock Avalable!, Quantity Cannot be Exceeded!!", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                                }
                                jsonObject.remove("ReQuantitySent");
                                jsonObject.put("ReQuantitySent", inputQty.toString());

                                msgReceived = Double.toString(roundTwoDecimals(jsonObject.getDouble("QuantitySent") - jsonObject.getDouble("QuantityReceived") - inputQty));
                                inputQty = Double.parseDouble(msgReceived);

                                jsonObject.remove("ReCalculatedQty");
                                jsonObject.put("ReCalculatedQty", inputQty.toString());
                                if (dataSourcePICKADHQuantityToPick.length() > j) {
                                    dataSourcePICKADHQuantityToPick.put(j, jsonObject);
                                } else dataSourcePICKADHQuantityToPick.put(jsonObject);
                                setLocalListView();
                            }catch(NumberFormatException e) {
                                MDToast.makeText(WMSTransferCard.this, "Not a Valid Number!", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                            }catch (Exception e) {
                                MDToast.makeText(getApplication(), "" + e, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            }
                        } else {
                            MDToast.makeText(getApplication(), "No Quantity Given!!", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                        }
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

    public void clearAlltheWeights(int j){
        try {
            JSONObject jsonObject = dataSourcePICKADHQuantityToPick.getJSONObject(j);
            jsonObject.remove("ReCalculatedQty");
            jsonObject.put("ReCalculatedQty", jsonObject.getString("QuantitySent"));
            jsonObject.remove("ReQuantitySent");
            jsonObject.put("ReQuantitySent", "0.00");
            dataSourcePICKADHQuantityToPick.put(j,jsonObject);
            setLocalListView();
            lv.setLayoutAnimation(animation);
            onItemsLoadComplete(refreshSelected);
        } catch (Exception e){}
    }


    public void onClickLinesDeatils() {
        final SweetAlertDialog builder = new SweetAlertDialog(WMSTransferCard.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Lines")
                .setCustomImage(R.drawable.alerttxtreport)
                .setContentText("Line Details :");
        View dialogView = WMSTransferCard.this.getLayoutInflater().inflate(R.layout.activity_details, null);
        TableLayout tableBrand= dialogView.findViewById(R.id.table);
        tableBrand.removeAllViews();
        String[] lineHeader = {"ItemCode","Description","BaseUOM","VariantCode",
                "FromStorageUnit","ToStorageUnit","QuantitySent",
                "QuantityReceiving","QuantityReceived","LineStatus","Barcode","LotNo",
                "OriginDate","ExpiryDate"};
        TableRow header = new TableRow(this);
        for (int i = 0; i < lineHeader.length; i++) {
            TextView tableHeader = new TextView(this);
            tableHeader.setTextColor(Color.BLACK);
            tableHeader.setTypeface(null, Typeface.BOLD);
            tableHeader.setGravity(Gravity.RIGHT);
            tableHeader.setText("  " + isNullChecker(lineHeader[i]) + "  ");
            tableHeader.setBackground(getResources().getDrawable(R.drawable.cell_header));
            tableHeader.startAnimation(AnimationUtils.loadAnimation(getApplication(),R.anim.fade_in));
            header.addView(tableHeader);
        }
        tableBrand.addView(header);

        try {
            for (int i = 0; i < dataLines.length(); i++) {
                JSONObject jsonObject = dataLines.getJSONObject(i);
                TableRow tbrow = new TableRow(this);
                for (int j = 0; j < lineHeader.length; j++) {
                    TextView tableHeader = new TextView(this);
                    tableHeader.setTextColor(Color.BLACK);
                    tableHeader.setGravity(Gravity.RIGHT);
                    tableHeader.setText("  " + isNullChecker(jsonObject.get(lineHeader[j]).toString()) + " ");
                    tableHeader.setBackground(getResources().getDrawable(R.drawable.cell_header));
                    tableHeader.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in));
                    tbrow.addView(tableHeader);
                }
                tableBrand.addView(tbrow);
            }
        }catch (JSONException e){}
        builder.setCustomView(dialogView);
        builder.setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
        builder.show();
    }

    public void onClickHeaderDeatils() {
        final SweetAlertDialog builder = new SweetAlertDialog(WMSTransferCard.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Header")
                .setCustomImage(R.drawable.alerttxtreport)
                .setContentText("Header Details :");
        View dialogView = WMSTransferCard.this.getLayoutInflater().inflate(R.layout.activity_details, null);
        TableLayout tableBrand= dialogView.findViewById(R.id.table);
        tableBrand.removeAllViews();
        String[] lineHeader = {"DocumentNo","DocumentDate","FromLocationCode","FromStorageUnitCode",
                "ToLocationCode","ToStorageUnitCode","InTransitCode","InTrasnitStorageUnitCode",
                "Reason","FromUserID","DocumentStatus","HandHeldStatus","NavStatus","QuickTransfer"};
        try {
            for (int i = 0; i < lineHeader.length; i++) {
                TableRow header = new TableRow(this);
                TextView tableHeader = new TextView(this);
                tableHeader.setTextColor(Color.BLACK);
                tableHeader.setTypeface(null, Typeface.BOLD);
                tableHeader.setGravity(Gravity.RIGHT);
                tableHeader.setText("  " + isNullChecker(lineHeader[i]) + "  ");
                tableHeader.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in));
                tableHeader.setBackground(getResources().getDrawable(R.drawable.cell_header));
                header.addView(tableHeader);

                JSONObject jsonObject = dataHeader.getJSONObject(0);
                tableHeader = new TextView(this);
                tableHeader.setTextColor(Color.BLACK);
                tableHeader.setGravity(Gravity.RIGHT);
                tableHeader.setText("  " + isNullChecker(jsonObject.get(lineHeader[i]).toString()) + "  ");
                tableHeader.setBackground(getResources().getDrawable(R.drawable.cell_header));
                tableHeader.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in));
                header.addView(tableHeader);

                tableBrand.addView(header);
            }
        } catch (JSONException e){} catch (Exception e){}
        builder.setCustomView(dialogView);
        builder.setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
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

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_orders, menu);

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
        if (id == android.R.id.home) {
            onFinishAcivity();
            return true;
        } else if (id == R.id.action_refresh) {
            indexOfList = null;
            setLocalListView();
            lv.setLayoutAnimation(animation);
            onItemsLoadComplete(refreshSelected);
            return true;
        } else if (id == R.id.action_header) {
            onClickHeaderDeatils();
            return true;
        } else if (id == R.id.action_lines) {
            onClickLinesDeatils();
            return true;
        } else if (id == R.id.action_bluetooth) {
            indexOfList = null;
            isAddMore = false;
            resetBluetoothConn(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        //Turn ON BlueTooth if it is OFF
        if(bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                Boolean syncConnPref = sharedPref.getBoolean("pref_bluetoothon",true);
                if(syncConnPref){
                    bluetoothAdapter.enable();
                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
            }
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Boolean syncConnPref = sharedPref.getBoolean("pref_bluetoothon",true);
            if(syncConnPref){
                bluetoothAdapter.enable();
            } else {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void setup() {
        dialog.setCancelable(true);
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
            ArrayList<String> pairedDeviceArrayNames = new ArrayList<String>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
                pairedDeviceArrayNames.add(device.getName());
            }

            ArrayAdapter<String> itemsusp=new ArrayAdapter<String>(WMSTransferCard.this,
                    android.R.layout.simple_list_item_1,android.R.id.text1,pairedDeviceArrayNames);
            listViewPairedDevice.setAdapter(itemsusp);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    BluetoothDevice device =
                            (BluetoothDevice) pairedDeviceArrayList.get(position);
                    MDToast.makeText(WMSTransferCard.this,
                            "Name: " + device.getName() + "\n"
                                    + "Address: " + device.getAddress(),
                            MDToast.LENGTH_LONG,MDToast.TYPE_INFO).show();

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            });
        } else {
            if(dialog.isShowing())
                dialog.dismiss();
            MDToast.makeText(WMSTransferCard.this,"No Bluetooth Paired Devices, Please Make sure that device should be paired!",
                    MDToast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            onFinishAcivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (matches != null && matches.size() > 0) {
                    String searchWrd = matches.get(0);
                    if (!TextUtils.isEmpty(searchWrd)) {
                        searchView.setQuery(searchWrd, false);
                    }
                }
            }
            return;
        } else if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                setup();
            }else{
                final SweetAlertDialog builder = new SweetAlertDialog(WMSTransferCard.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Bluetooth Setup")
                        .setCustomImage(R.drawable.alertexit)
                        .setContentText("BlueTooth NOT enabled, Are you sure want to EXIT");
                builder.setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                finish();
                            }
                        });
                builder.setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent tender=new Intent(WMSTransferCard.this,WMSGRCard.class);
                                startActivity(tender);
                            }
                        });
                builder.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){


        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                textStatus.animateText("Establishing Connection..");
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AlertFailedSound();
                        Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AlertFailedSound();
                        MDToast.makeText(getApplicationContext(),"Unable to Connect: "+ eMessage,
                                MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                    }
                });
            }

            if(success){
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textInfo.setText(bluetoothDevice.getName());
                        AlertSound();
                        if(indexOfList != null) {
                            if (!dialog.isShowing())
                                setBletooth();
                        }
                    }});
                startThreadConnected(bluetoothSocket);
            } else {
                try {
                    if(indexOfList != null) {
                        if (!dialog.isShowing())
                            setBletooth();
                    }
                }catch (Exception e){}
            }
        }

        public void cancel() {
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (Exception e) {}
            }
            if (bluetoothSocket.isConnected()) {
                try {
                    bluetoothSocket.close();
                } catch (Exception e) {}
            }
        }

    }

    public void resetBluetoothConn(Boolean type) {
        if(type){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            type = sharedPref.getBoolean("pref_bluetooth_disconnect",false);
        } else {
            type = true;
        }
        if (type) {
            if (myThreadConnectBTdevice != null ? myThreadConnectBTdevice.bluetoothSocket != null : false) {
                try {
                    myThreadConnectBTdevice.cancel();
                    myThreadConnected.cancel();
                    myThreadConnectBTdevice.interrupt();
                    myThreadConnected.interrupt();
                    myThreadConnectBTdevice = null;
                    myThreadConnected = null;
                } catch (Exception e) {
                    if (!dialog.isShowing())
                        setBletooth();
                }
            } else {
                if (!dialog.isShowing())
                    setBletooth();
            }
        }
    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        String displayValue = "";

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;

        }

        @Override
        public void run() {
            byte[] buffer = new byte[512];
            int bytes;
            boolean isContinue = true;

            while (isContinue) {
                try {
                    if (this.connectedInputStream != null && myThreadConnectBTdevice.bluetoothSocket.isConnected()) {
                        bytes = connectedInputStream.read(buffer);
                        final String strReceived = new String(buffer, 0, bytes);

                        String[] parts = strReceived.split("\\r?\\n");

                        if (parts.length >= 2) {
                            if(!displayValue.equals(parts[1])){
                                displayValue = parts[1];
                                msgReceived = displayValue;
                                WMSTransferCard.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        AlertSound();
                                        WMSTransferCard.this.textStatus.animateText(msgReceived);
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    isContinue = false;
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            if(!isFinishing()) {
                                AlertFailedSound();
                                MDToast.makeText(getApplicationContext(), "Connection lost!",
                                        MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                            }
                        }});
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void AlertSound(){
        if(dialog.isShowing()) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPref.getBoolean("pref_notification", true)) {
                try {
                    toneGen1.startTone(ToneGenerator.TONE_SUP_ERROR, 300);
                } catch (Exception e) {}
            }
        } else {
            if(indexOfList == null)
                MDToast.makeText(getApplicationContext(), "Successfully Connected!",
                        MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
        }
    }

    public void AlertFailedSound(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("pref_notification",true)) {
            try {
                toneGen1.startTone(ToneGenerator.TONE_SUP_ERROR, 300);
            } catch (Exception e){}
        }
    }

    public void onFinishAcivity(){
        final SweetAlertDialog builder = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Are you sure ?")
                .setCustomImage(R.drawable.alertexit)
                .setContentText("Are you sure want to Back to List");
        builder.setConfirmText("YES BACK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        MyStatic.setWhichactivity("TRANSFER");
                        //startActivityForResult(new Intent(WMSTransferCard.this,JobViewActivity.class),1);
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

}