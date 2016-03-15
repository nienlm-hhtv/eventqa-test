package com.hhtv.eventqa.helper.listener;

/**
 * Created by nienb on 12/3/16.
 */
public interface IOnAdapterInteractListener {
    void processVote(int questionId, boolean up);
    void scroll(int position);
}
