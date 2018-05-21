package id.ac.binus.boarderlognightmonitoring.services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.ac.binus.boarderlognightmonitoring.model.service.WsResponse;

/**
 * Created by CT on 05-Apr-17.
 */

public class JsonPostRequest extends JsonObjectRequest {

    private static final int BACKOFF_TIMEOUT = 10000;
    private static final int BACKOFF_RETRY = 3;

    private boolean isUsingHeader = false;

    public JsonPostRequest(boolean isUsingHeader, String url, JSONObject jsonRequest, final WsListener callback, final WsResponse wsResponse) {
        this(isUsingHeader, url, jsonRequest, callback, wsResponse, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
    }

    public JsonPostRequest(final boolean isUsingHeader, final String url, final JSONObject jsonRequest, final WsListener callback, final WsResponse wsResponse, final int retry) {
        super(Request.Method.POST, url, jsonRequest, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {

                try {
                    Log.v("response: ", arg0.toString(1) + "");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                try {
                    wsResponse.parse(arg0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (wsResponse.isSuccess())
                    callback.onSuccess(wsResponse);
                else
                    callback.onError(wsResponse.getErrorMessage());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", error.getMessage() + "");

                callback.onError(error.getMessage());
                //if (error.networkResponse!=null && error.networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED)
                //Util.forceLogOut();
            }
        });

        this.isUsingHeader = isUsingHeader;

        setRetryPolicy(new DefaultRetryPolicy(BACKOFF_TIMEOUT,
                retry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        try {
            Log.v("FS", jsonRequest.toString());
        } catch (Exception e) {

        }

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> header = new HashMap<>();
        //header.put("Content-Type", "application/json");
        return header;
    }

    @Override
    public Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        //For NO CONTENT response, do not try to parseContent it as json object,
        // instead create empty json object with success status
        if (response.statusCode == HttpStatus.SC_NO_CONTENT) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("status", "SUCCESS");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Response.success(jsonObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        }


        return super.parseNetworkResponse(response);
    }
}
