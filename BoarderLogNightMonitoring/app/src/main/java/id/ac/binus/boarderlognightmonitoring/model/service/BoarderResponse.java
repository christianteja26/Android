package id.ac.binus.boarderlognightmonitoring.model.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.ac.binus.boarderlognightmonitoring.litehelper.LiteHelper;

/**
 * Created by CT on 28-Apr-17.
 */

public class BoarderResponse extends WsResponse {
    private LiteHelper DB;

    public BoarderResponse(Context context) {
        DB = new LiteHelper(context);
    }

    @Override
    protected void parseContent(JSONObject jsonObject) throws JSONException {
        super.parseContent(jsonObject);
        DB.open();
        JSONArray activeBoarderArrayObject = jsonObject.getJSONArray("activeBoarders");
        for (int i = 0; i < activeBoarderArrayObject.length(); i++) {
            JSONObject activeBoarderObject = activeBoarderArrayObject.getJSONObject(i);
            String registrationID = activeBoarderObject.getString("RegistrationID");
            String binusianID = activeBoarderObject.getString("BinusianID");
            String NIM = activeBoarderObject.getString("NIM");
            String boarderName = activeBoarderObject.getString("BoarderName");
            String cardID = activeBoarderObject.getString("CardID");
            String photo = activeBoarderObject.getString("Photo");
            DB.updateActiveBoarder(registrationID,binusianID,NIM,boarderName,cardID,photo);
            DB.insertActiveBoarder(registrationID,binusianID,NIM,boarderName,cardID,photo);
        }
        DB.close();
    }
}
