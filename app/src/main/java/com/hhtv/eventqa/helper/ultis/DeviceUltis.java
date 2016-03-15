package com.hhtv.eventqa.helper.ultis;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by nienb on 8/3/16.
 */
public class DeviceUltis {
    public static String getDeviceId(Context mContext){
        TelephonyManager tManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        if (uuid != ""){
            return "mobile-" + uuid;
        }else{
            return "mobile-" + getSavedUUID(mContext);
        }
    }

    public static String getSavedUUID(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        String s = spfr.getString("uuid", "");
        if (s == ""){
            s = nextSessionId();
            setSavedUUID(mContext,s);
        }
        return s;
    }

    public static void setSavedUUID(Context mContext, String s) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putString("uuid", s);
        editor.commit();
    }


    public static String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(24);
    }
}
