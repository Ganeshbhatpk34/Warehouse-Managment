package com.probatus.rhbus.warehouse.JsonParse;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by ganapathi on 12/4/19.
 */

public class JsonResponse {

    public void JsonResponse(
            final Context thisC,
            String serviceName,
            final String methodName,
            ArrayList<String> list,
            final HttpResponse<JSONObject> callback1){

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParam = null;
        try {
            jsonParam = new JSONObject();
            jsonParam.put("serviceName", serviceName);
            jsonParam.put("methodName", methodName);
            jsonParam.put("parameters", new JSONArray(list));
        }catch (JSONException e){}
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParam.toString());
        }catch (IOException e){}

        client.post(thisC, "https://rhbussupport.com/rhbusphp/Amfphp/index.php", entity,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.e("Tag","Started");
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode,headers,response);
                //Log.e("Tag",""+response);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("result", response);
                    if (callback1 != null) {
                        callback1.onResponse(jsonObject);
                    }
                }catch (JSONException e){
                    Log.e("Tag",""+e);
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode,headers,response);
                //Log.e("result", ""+response);
                if (callback1 != null) {
                    callback1.onResponse(response);
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.String responseString){
                super.onSuccess(statusCode,headers,responseString);
                //Log.e("",""+responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONArray errorResponse){
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(methodName.equals("inCreds")){
                    MDToast.makeText(thisC,"Username/Password Incorrect, Please Check Again!"+ errorResponse,MDToast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
                } else {
                    MDToast.makeText(thisC, "Error from Server! Please Try Again After Some Time: " + errorResponse, MDToast.LENGTH_LONG, MDToast.TYPE_INFO).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse){
                super.onFailure(statusCode, headers,  throwable, errorResponse);
                if(methodName.equals("inCreds")){
                    MDToast.makeText(thisC,"Username/Password Incorrect, Please Check Again!"+ errorResponse,MDToast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
                } else {
                    MDToast.makeText(thisC, "Error from Server! Please Try Again After Some Time: " + errorResponse, MDToast.LENGTH_LONG, MDToast.TYPE_INFO).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, java.lang.String responseString, java.lang.Throwable throwable){
                super.onFailure(statusCode, headers, responseString, throwable);
                if(methodName.equals("inCreds")){
                    MDToast.makeText(thisC,"Username/Password Incorrect, Please Check Again!"+ responseString,MDToast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
                } else {
                    MDToast.makeText(thisC, "Error from Server! Please Try Again After Some Time: " + responseString, MDToast.LENGTH_LONG, MDToast.TYPE_INFO).show();
                }
            }

            public void onFailure(Throwable e, JSONObject response){
                if(methodName.equals("inCreds")){
                    MDToast.makeText(thisC,"Username/Password Incorrect, Please Check Again!"+ response,MDToast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
                } else {
                    MDToast.makeText(thisC, "Error from Server! Please Try Again After Some Time: " + response, MDToast.LENGTH_LONG, MDToast.TYPE_INFO).show();
                }
            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }

            @Override
            protected  Object parseResponse(byte[] responseBody) throws JSONException {
                if (null == responseBody)
                    return null;
                Object result = null;
                //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If JSON is not valid this will return null
                String jsonString = getResponseString(responseBody, getCharset());
                if (jsonString != null) {
                    jsonString = jsonString.trim();
                    if (jsonString.startsWith(UTF8_BOM)) {
                        jsonString = jsonString.substring(1);
                    }
                    if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                        result = new JSONTokener(jsonString).nextValue();
                    }
                }
                if (result == null) {
                    result = jsonString;
                    Log.e("ERROR",""+result);
                }
                return result;
            }
        });

    }
}
