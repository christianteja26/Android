package id.ac.binus.boarderlognightmonitoring.model.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CT on 05-Apr-17.
 */

public class SyncDateResponse extends WsResponse {
    private Integer TotalRec;

    @Override
    protected void parseContent(JSONObject jsonObject) throws JSONException {
        super.parseContent(jsonObject);

        JSONArray reasonArrayObject = jsonObject.getJSONArray("activeBoarderCounts");
        for (int i = 0; i < reasonArrayObject.length(); i++) {
            JSONObject reasonObject = reasonArrayObject.getJSONObject(i);
            TotalRec = Integer.parseInt(reasonObject.getString("TotalRec"));
        }
    }

    public int getTotalRec(){
        return TotalRec;
    }
}
