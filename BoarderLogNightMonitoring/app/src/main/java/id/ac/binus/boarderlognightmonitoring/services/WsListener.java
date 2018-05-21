package id.ac.binus.boarderlognightmonitoring.services;

import id.ac.binus.boarderlognightmonitoring.model.service.WsResponse;

/**
 * Created by CT on 05-Apr-17.
 */

public interface WsListener<T extends WsResponse> {
    void onSuccess(T response);

    void onError(String error);
}
