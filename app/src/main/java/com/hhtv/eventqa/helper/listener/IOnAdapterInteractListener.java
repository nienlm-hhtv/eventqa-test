package com.hhtv.eventqa.helper.listener;

import com.hhtv.eventqa.model.question.Result;

/**
 * Created by nienb on 12/3/16.
 */
public interface IOnAdapterInteractListener {
    void processVote(Result question, int pos,  boolean up);
    void scroll(int position);
    void showToast(String toast);
}
