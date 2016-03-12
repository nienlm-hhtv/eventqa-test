package com.hhtv.eventqa.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by nienb on 7/3/16.
 */
public class BaseFragment extends Fragment{

    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    protected Context getRealContext(){
        return getContext() == null ? mContext : getContext();
    }
}
