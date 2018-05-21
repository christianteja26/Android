package id.ac.binus.boarderlognightmonitoring.services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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

public class JsonGetRequest extends JsonObjectRequest {

    private static final int BACKOFF_TIMEOUT = 6000000;
    private boolean isUsingHeader = false;
    private WsResponse wsResponse;

    public JsonGetRequest(boolean isUsingHeader, String url, final WsListener callback, final WsResponse wsResponse) {
        this(isUsingHeader, url, callback, wsResponse, DefaultRetryPolicy.DEFAULT_MAX_RETRIES);
    }

    public JsonGetRequest(final boolean isUsingHeader, final String url, final WsListener callback, final WsResponse _wsResponse,
                          final int retry) {
        super(Method.GET, url, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("response: ", response.toString(1) + "");
                } catch (JSONException e1) {
                }
                callback.onSuccess(_wsResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.getMessage());
            }
        });

        this.isUsingHeader = isUsingHeader;
        this.wsResponse = _wsResponse;

        Log.v("url", url);

        setRetryPolicy(new DefaultRetryPolicy(BACKOFF_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> header = new HashMap<>();
        return header;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        //For NO CONTENT response, do not try to parseContent it as json object,
        // instead create empty json object with success status
        if (response.statusCode == HttpStatus.SC_NO_CONTENT) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("status", "SUCCESS");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                wsResponse.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Response.success(jsonObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        }

        Response<JSONObject> jsonObjectResponse = super.parseNetworkResponse(response);
        try {
            wsResponse.parse(jsonObjectResponse.result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjectResponse;
    }
}
