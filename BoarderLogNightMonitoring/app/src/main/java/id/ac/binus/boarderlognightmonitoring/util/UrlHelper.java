package id.ac.binus.boarderlognightmonitoring.util;

import android.content.Context;

import id.ac.binus.boarderlognightmonitoring.R;

/**
 * Created by CT on 05-Apr-17.
 */

public final class UrlHelper {

    private static UrlHelper sInstance;
    public final String baseUrl;

    private UrlHelper(Context context) {
        baseUrl = context.getString(R.string.ptr_host);
    }

    public static UrlHelper getInstance() {
        return sInstance;
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new UrlHelper(context);
        }
    }

    public String getUrl(String path) {
        return baseUrl + path;
    }
}
