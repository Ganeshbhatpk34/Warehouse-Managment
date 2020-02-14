package com.probatus.rhbus.warehouse.JsonParse;

/**
 * Created by ganapathi on 12/4/19.
 */

public interface HttpResponse<JSONObject>  {
    void onResponse(org.json.JSONObject response);
}
