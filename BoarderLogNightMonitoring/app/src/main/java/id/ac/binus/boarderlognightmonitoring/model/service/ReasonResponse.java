package id.ac.binus.boarderlognightmonitoring.model.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;

/**
 * Created by CT on 05-Apr-17.
 */

public class ReasonResponse extends WsResponse {
    private LiteHelper DB;

    public ReasonResponse(Context context) {
        DB = new LiteHelper(context);
    }

    @Override
    protected void parseContent(JSONObject jsonObject) throws JSONException {
        super.parseContent(jsonObject);

        DB.open();
        JSONArray reasonArrayObject = jsonObject.getJSONArray("reasons");
        for (int i = 0; i < reasonArrayObject.length(); i++) {
            JSONObject reasonObject = reasonArrayObject.getJSONObject(i);
            Integer reasonID = Integer.parseInt(reasonObject.getString("ReasonID"));
            String reasonName = reasonObject.getString("ReasonName");
            DB.insertReason(reasonID,reasonName);
        }
        DB.close();
    }
}
