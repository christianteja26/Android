package id.ac.binus.boarderlognightmonitoring.model.service;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CT on 05-Apr-17.
 */

public class WsResponse {

    /**
     * Internal JSONObject of the response
     */
    private JSONObject jsonObject;

    /**
     * Flag indicating the operations result
     */
    private boolean isSuccess;

    /**
     * Message of operation. It differ from error message, that will show on onError in @class WsListener
     */
    private String error;

    /**
     * Parse the JSON Object and HTTP Status Code from network operation
     *
     * @param jsonObject jsonObject from network response body
     */
    public void parse(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;

        if (jsonObject != null) {
            isSuccess = jsonObject.getBoolean("success");
            error = jsonObject.optString("error");

            if (isSuccess()) {
                try {
                    parseContent(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Subclass must override this method to process JSON Object
     *
     * @param jsonObject JSON Object to be processed
     */
    protected void parseContent(JSONObject jsonObject) throws JSONException {
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getErrorMessage() {
        return error;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

}
