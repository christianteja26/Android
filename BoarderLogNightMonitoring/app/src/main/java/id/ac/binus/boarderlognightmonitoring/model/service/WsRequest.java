package id.ac.binus.boarderlognightmonitoring.model.service;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import id.ac.binus.boarderlognightmonitoring.services.WsListener;
import id.ac.binus.boarderlognightmonitoring.util.ErrorMessageHelper;
import id.ac.binus.boarderlognightmonitoring.util.UrlHelper;

/**
 * Created by CT on 05-Apr-17.
 */

public class WsRequest<T extends WsResponse> extends JsonRequest<T> {

    /**
     * Default timeout for webservice call
     */
    private static final int TIMEOUT = 10000;

    /**
     * Response instance
     */
    private final T mResponse;

    /**
     * Callback instance
     */
    private final WsListener<T> mListener;

    /**
     * Flag indicating bearer token usage in the header
     */
    private final boolean isUsingBearerToken;

    public WsRequest(int method, String path, boolean isUsingBearerToken, JSONObject jsonRequest, T response, WsListener<T> listener) {
        super(method, UrlHelper.getInstance().getUrl(path), (jsonRequest == null) ? null : jsonRequest.toString(), null, null);
        this.mListener = listener;
        this.isUsingBearerToken = isUsingBearerToken;
        this.mResponse = response;

        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        setTag(listener);

        try {
            Log.v("request", path);
            Log.v("request", (jsonRequest == null) ? "GET" : jsonRequest.toString(1) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convinience method for performing GET request
     *
     * @param path               target endpoint
     * @param isUsingBearerToken Flag indicating bearer token usage in the header
     * @param response           response instance
     * @param listener           callback instance
     * @param <T>                Response Type
     * @return Request Object
     */
    public static <T extends WsResponse> WsRequest<T> Get(String path, boolean isUsingBearerToken,
                                                          T response, WsListener<T> listener) {
        return new WsRequest<>(Method.GET, path, isUsingBearerToken, null, response, listener);
    }

    /**
     * Convinience method for performing POST request
     *
     * @param path               target endpoint
     * @param isUsingBearerToken Flag indicating bearer token usage in the header
     * @param jsonObject         Request Body
     * @param response           response instance
     * @param listener           callback instance
     * @param <T>                Response Type
     * @return Request Object
     */
    public static <T extends WsResponse> WsRequest<T> Post(String path, boolean isUsingBearerToken,
                                                           JSONObject jsonObject, T response,
                                                           WsListener<T> listener) {
        return new WsRequest<>(Method.POST, path, isUsingBearerToken, jsonObject, response, listener);
    }

    /**
     * Convinience method for performing PUT request
     *
     * @param path               target endpoint
     * @param isUsingBearerToken Flag indicating bearer token usage in the header
     * @param jsonObject         Request Body
     * @param response           response instance
     * @param listener           callback instance
     * @param <T>                Response Type
     * @return Request Object
     */
    public static <T extends WsResponse> WsRequest<T> Put(String path, boolean isUsingBearerToken,
                                                          JSONObject jsonObject, T response,
                                                          WsListener<T> listener) {
        return new WsRequest<>(Method.PUT, path, isUsingBearerToken, jsonObject, response, listener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> header = new HashMap<>();
        return header;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        JSONObject jsonContent = null;

        if (response.statusCode != HttpStatus.SC_NO_CONTENT) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                jsonContent = new JSONObject(jsonString);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }

        try {
            Log.v("response", response.statusCode + "");
            if (jsonContent != null)
                Log.v("jsonContent", jsonContent.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (response.statusCode == HttpStatus.SC_UNAUTHORIZED) {
            return Response.error(new VolleyError(response));
        }

        if (response.statusCode < 200 || response.statusCode > 299) {
            return Response.error(new VolleyError(getError(jsonContent)));
        }

        try {
            mResponse.parse(jsonContent);
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }

        if (mResponse.isSuccess()) {
            return Response.success(mResponse, HttpHeaderParser.parseCacheHeaders(response));
        } else {
            return Response.error(new VolleyError(mResponse.getErrorMessage()));
        }
    }

    private String getError(JSONObject jsonObject) {

        try {
            Object dataObject = jsonObject.get("data");

            if (dataObject instanceof JSONArray) {
                JSONArray data = (JSONArray) dataObject;
                String error = data.getString(0);
                if (error.length() > 0)
                    return error;
            } else if (dataObject instanceof JSONObject) {
                Iterator<String> keys = ((JSONObject) dataObject).keys();
                if (keys.hasNext()) {

                    Object firstObject = ((JSONObject) dataObject).get(keys.next());
                    if (firstObject instanceof JSONArray) {
                        JSONArray data = (JSONArray) firstObject;
                        String error = data.getString(0);
                        if (error.length() > 0)
                            return error;
                    } else {
                        return firstObject.toString();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ErrorMessageHelper.getInstance().GENERIC;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onSuccess(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {

        String userMessage;

        if (error instanceof NoConnectionError) {
            userMessage = ErrorMessageHelper.getInstance().NO_INTERNET_CONNECTION;
        } else if (error instanceof TimeoutError) {
            userMessage = ErrorMessageHelper.getInstance().SERVER_ERROR;
        } else if (error instanceof ServerError) {
            userMessage = ErrorMessageHelper.getInstance().SERVER_ERROR;
        } else if (!TextUtils.isEmpty(error.getMessage())) {
            userMessage = error.getMessage();
        } else if (error.networkResponse != null) {
            try {
                String jsonString = new String(error.networkResponse.data,
                        HttpHeaderParser.parseCharset(error.networkResponse.headers, PROTOCOL_CHARSET));
                JSONObject jsonContent = new JSONObject(jsonString);

                try {
                    Log.v("response", error.networkResponse.statusCode + "");
                    Log.v("jsonContent", jsonContent.toString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                userMessage = getError(jsonContent);
            } catch (UnsupportedEncodingException | JSONException e) {
                userMessage = ErrorMessageHelper.getInstance().GENERIC;
            }
        } else {
            userMessage = ErrorMessageHelper.getInstance().GENERIC;
        }

        if (mListener != null) {
            mListener.onError(userMessage);
        }
    }
}
