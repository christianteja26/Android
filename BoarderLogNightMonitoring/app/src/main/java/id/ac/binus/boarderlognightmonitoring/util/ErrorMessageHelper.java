package id.ac.binus.boarderlognightmonitoring.util;

import android.content.Context;

import id.ac.binus.boarderlognightmonitoring.R;

/**
 * Created by CT on 05-Apr-17.
 */

public final class ErrorMessageHelper {

    private static ErrorMessageHelper sInstance;

    public final String NO_INTERNET_CONNECTION;
    public final String GENERIC;
    public final String SERVER_ERROR;
    public final String TIMEOUT_ERROR;

    private ErrorMessageHelper(Context context) {
        NO_INTERNET_CONNECTION = context.getString(R.string.error_no_internet_connection);
        GENERIC = context.getString(R.string.server_error);
        TIMEOUT_ERROR = context.getString(R.string.error_timeout);
        SERVER_ERROR = GENERIC;
    }

    public static ErrorMessageHelper getInstance() {
        return sInstance;
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new ErrorMessageHelper(context);
        }
    }
}
