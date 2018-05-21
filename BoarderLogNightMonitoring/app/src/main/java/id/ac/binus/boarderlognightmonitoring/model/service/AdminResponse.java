package id.ac.binus.boarderlognightmonitoring.model.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;

/**
 * Created by CT on 03-May-17.
 */

public class AdminResponse extends WsResponse {
    private LiteHelper DB;

    public AdminResponse(Context context) {
        DB = new LiteHelper(context);
    }

    @Override
    protected void parseContent(JSONObject jsonObject) throws JSONException {
        super.parseContent(jsonObject);

        DB.open();
        JSONArray adminArrayObject = jsonObject.getJSONArray("configs");
        for (int i = 0; i < adminArrayObject.length(); i++) {
            JSONObject adminObject = adminArrayObject.getJSONObject(i);
            Integer configID = Integer.parseInt(adminObject.getString("ConfigID"));
            String configName = adminObject.getString("ConfigName");
            String value = adminObject.getString("Value");
            DB.insertAdmin(configID,configName,value);
        }
        DB.close();
    }
}
