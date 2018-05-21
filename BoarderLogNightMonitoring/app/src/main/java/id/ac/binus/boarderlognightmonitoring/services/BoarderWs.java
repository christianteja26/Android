package id.ac.binus.boarderlognightmonitoring.services;

import android.content.Context;
import android.database.Cursor;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;
import id.ac.binus.boarderlognightmonitoring.model.service.AdminResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.BoarderOutResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.BoarderResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.ReasonResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.SyncDateResponse;
import id.ac.binus.boarderlognightmonitoring.model.service.WsResponse;
import id.ac.binus.boarderlognightmonitoring.util.Util;

/**
 * Created by CT on 05-Apr-17.
 */

public class BoarderWs {
    private static RequestQueue sRequestQueue;
    private Context mContext;
    private LiteHelper db;

    public BoarderWs(Context context) {
        this.mContext = context;
        db = new LiteHelper(mContext);
        if (sRequestQueue == null)
            sRequestQueue = BoarderVolley.newRequestQueue(context);
    }

    public static void clearQueue() {

        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
            sRequestQueue.stop();
            sRequestQueue = null;
        }
    }

    //    User Functionality=================================================================================================================
    public void getReasonData(WsListener callback) {
        sRequestQueue.add(new JsonGetRequest(false, Util.TAKE_REASONS_URL, callback, new ReasonResponse(mContext)));
    }

    public void getBoarderData(WsListener callback){
        sRequestQueue.add(new JsonGetRequest(false, Util.ACTIVE_BOARDER_URL, callback, new BoarderResponse(mContext)));
    }

    public void getAdminData(WsListener callback){
        sRequestQueue.add(new JsonGetRequest(false, Util.ADMIN_URL, callback, new AdminResponse(mContext)));
    }

    public void postLastSyncDate(WsListener callback)
    {
        JSONObject wrapperObject = new JSONObject();
        db.open();
        Cursor cLastSyncDate = db.getLastSyncDate(1);
        if(cLastSyncDate.moveToFirst())
        {
            try{
                wrapperObject.put("LastSyncDate",cLastSyncDate.getString(cLastSyncDate.getColumnIndex(LiteHelper.KEY_SYNC_LASTSYNCDATE)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        else
        {
            try{
                wrapperObject.put("LastSyncDate","");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        db.close();
        sRequestQueue.add((new JsonPostRequest(false, Util.SYNC_DATE_URL, wrapperObject, callback, new SyncDateResponse())));
    }

    public void postToGetCheckOutList(WsListener callback)
    {
        JSONObject wrapperObject = new JSONObject();
        db.open();
        Cursor cLastSyncDate = db.getLastSyncDate(1);
        if(cLastSyncDate.moveToFirst())
        {
            try{
                wrapperObject.put("LastSyncDate",cLastSyncDate.getString(cLastSyncDate.getColumnIndex(LiteHelper.KEY_SYNC_LASTSYNCDATE)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        else
        {
            try{
                wrapperObject.put("LastSyncDate","");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        db.close();
        sRequestQueue.add((new JsonPostRequest(false, Util.BOARDER_OUT_URL, wrapperObject, callback, new BoarderOutResponse(mContext))));
    }

    public void postParamToGetBoarderData(WsListener callback, int batch, int rowPerBatch)
    {
        JSONObject wrapper = new JSONObject();

        try {
            wrapper.put("Batch", batch);
            wrapper.put("RowPerBatch", rowPerBatch);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sRequestQueue.add((new JsonPostRequest(false, Util.ACTIVE_BOARDER_URL, wrapper, callback, new BoarderResponse(mContext))));
    }

    public void postLocalData(WsListener callback) {
        JSONObject wrapperDataObject = new JSONObject();
        JSONArray wrapperDataArray = new JSONArray();
        JSONObject wrapperArrayObject = new JSONObject();
        db.open();
        Cursor cLog = db.getAllLog();
        if(cLog.moveToFirst())
        {
            do{
                try {
                    wrapperArrayObject = new JSONObject();
                    wrapperArrayObject.put("BoarderLogNightMonitoringID",cLog.getString(cLog.getColumnIndex(LiteHelper.KEY_LOG_LOGID)));
                    wrapperArrayObject.put("RegistrationID",cLog.getString(cLog.getColumnIndex(LiteHelper.KEY_LOG_REGISTRATIONID)));
                    wrapperArrayObject.put("CheckOutDate",cLog.getString(cLog.getColumnIndex(LiteHelper.KEY_LOG_CHECKOUTDATE)));
                    wrapperArrayObject.put("CheckInDate",cLog.getString(cLog.getColumnIndex(LiteHelper.KEY_LOG_CHECKINDATE)));
                    wrapperArrayObject.put("ReasonName",cLog.getString(cLog.getColumnIndex(LiteHelper.KEY_LOG_REASONNAME)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    wrapperDataArray.put(wrapperArrayObject);
                    wrapperDataObject.put("Data",wrapperDataArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }while(cLog.moveToNext());
        }
        db.close();
        sRequestQueue.add((new JsonPostRequest(false, Util.LOG_NIGHT_URL, wrapperDataObject, callback, new WsResponse())));
    }

    /*public void Login(WsListener callback, String username, String password){
        JSONObject wrapper = new JSONObject();

        try {
            wrapper.put("username", username);
            wrapper.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sRequestQueue.add((new JsonPostRequest(false, Util.LOGIN_URL, wrapper, callback, new WsResponse())));
    }*/
}
