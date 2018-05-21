package id.ac.binus.boarderlognightmonitoring.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;

import id.ac.binus.boarderlognightmonitoring.R;

/**
 * Created by CT on 05-Apr-17.
 */

public class Util {
    public static final int SPLASH_SCREEN_DURATION = 2000;
    private static final String BASE_URL = "http://202.58.181.203/BoarderLogNightMonitoring/api/";
    public static final String TAKE_REASONS_URL = BASE_URL + "reason/reasondata";
    public static final String ADMIN_URL = BASE_URL + "configuration/login";
    public static final String ACTIVE_BOARDER_URL = BASE_URL + "activeboarder/activeboarderdata";
    public static final String SYNC_DATE_URL = BASE_URL + "activeboarder/activeboardercount";
    public static final String LOG_NIGHT_URL = BASE_URL + "lognight/lognightdata";
    public static final String BOARDER_OUT_URL = BASE_URL + "activeboarder/boardercheckoutdata";
    public static final Integer AppConfigID = 37;
    private static Context context;
    private static Handler saverHandler;
    private static HandlerThread handlerThread;
    public static final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    static {
        handlerThread = new HandlerThread("SaverHandlerThread");
        handlerThread.start();
        saverHandler = new Handler(handlerThread.getLooper());
    }

    public static void setContext(Context context) {
        // Get Application Context instead of Activity Context to prevent activity leak
        if (Util.context == null)
            Util.context = context.getApplicationContext();
    }

    public static String convertMACAddresstoBase36(String macAddress) {

        String[] temp = macAddress.split(":");

        StringBuilder builder = new StringBuilder();

        for(String a : temp) {
            builder.append(a);
        }

        return convertHexToBase36(builder.toString());
    }

    public static String convertHexToBase36(String hex) {
        BigInteger big = new BigInteger(hex, 16);
        return big.toString(36);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("tag:");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    public static byte[] getBytesFromBase64String(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }

    public static Spanned formatPlaceDetails(Resources res, CharSequence name,
                                             CharSequence address) {
        Log.e("RP", res.getString(R.string.place_details, name, address));
        return Html.fromHtml(res.getString(R.string.place_details, name, address));
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
