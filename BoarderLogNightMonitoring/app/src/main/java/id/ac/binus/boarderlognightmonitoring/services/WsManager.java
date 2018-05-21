package id.ac.binus.boarderlognightmonitoring.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import id.ac.binus.boarderlognightmonitoring.model.service.WsRequest;

/**
 * Created by CT on 05-Apr-17.
 */

public class WsManager {
    private static WsManager sInstance;

    private RequestQueue mRequestQueue;

    private WsManager(Context context) {
        mRequestQueue = BoarderVolley.newRequestQueue(context);
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new WsManager(context);
        }
    }

    public static WsManager getInstance() {
        return sInstance;
    }

    public void addToQueue(WsRequest request) {
        mRequestQueue.add(request);
    }

    public void clear(Object tag) {
        if (tag != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void clearAll() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }
}
