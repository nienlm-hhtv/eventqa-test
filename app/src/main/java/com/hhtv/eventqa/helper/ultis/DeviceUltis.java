package com.hhtv.eventqa.helper.ultis;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import hugo.weaving.DebugLog;

/**
 * Created by nienb on 8/3/16.
 */
public class DeviceUltis {
    public static String getDeviceId(Context mContext){
        TelephonyManager tManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        /*if (uuid != ""){
            return "mobile-" + uuid;
        }else{
            return "mobile-" + getSavedUUID(mContext);
        }*/
        if (uuid == null){
            uuid = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return "mobile-" + uuid;
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
        editor.apply();
    }


    public static String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(24);
    }

    @DebugLog
    public static String toSHA1(String text) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(text.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++)
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
