package com.hhtv.eventqa.helper.listener;

/**
 * Created by nienb on 12/3/16.
 */
public interface IOnAdapterInteractListener {
    void processVote(int id, int pos,  boolean up);
    void scroll(int position);
    void showToast(String toast);
}
