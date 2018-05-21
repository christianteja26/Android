package id.ac.binus.boarderlognightmonitoring.services;

import id.ac.binus.boarderlognightmonitoring.model.service.WsResponse;

/**
 * Created by CT on 08-Feb-18.
 */

public interface GroupWsListener<T extends WsResponse> {
    void onSuccess(T response, String groupTag);

    void onError(String error, String groupTag);
}
