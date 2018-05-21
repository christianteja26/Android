package id.ac.binus.boarderlognightmonitoring.model.service;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;

/**
 * Created by CT on 07-Jul-17.
 */

public class BoarderOutResponse extends WsResponse {
    private LiteHelper db;

    public BoarderOutResponse(Context context) {
        db = new LiteHelper(context);
    }

    @Override
    protected void parseContent(JSONObject jsonObject) throws JSONException {
        super.parseContent(jsonObject);

        /*db.open();
        JSONArray reasonArrayObject = jsonObject.getJSONArray("checkOutBoarders");
        for (int i = 0; i < reasonArrayObject.length(); i++) {
            JSONObject reasonObject = reasonArrayObject.getJSONObject(i);
            String registrationID = reasonObject.getString("RegistrationID");
            db.deleteActiveBoarder(registrationID);
        }
        db.close();*/
    }
}
