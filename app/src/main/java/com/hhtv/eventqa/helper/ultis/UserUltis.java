package com.hhtv.eventqa.helper.ultis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by nienb on 2/3/16.
 */
public class UserUltis {
    public static int getUserId(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return spfr.getInt("1",-1);
    }

    public static void setUserId(Context mContext, int id) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putInt("1", id);
        editor.commit();
    }

    public static String getUserName(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return spfr.getString("2", "");
    }

    public static void setUserName(Context mContext, String name) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putString("2", name);
        editor.commit();
    }

    public static String getUserEmail(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return spfr.getString("3","");
    }

    public static void setUserEmail(Context mContext, String email) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putString("3", email);
        editor.commit();
    }

    public static void getAllSharePref(Context context, String filename) {
        SharedPreferences prefs = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("MYTAG: ","SPRF: " +  entry.getKey() + " : "
                    + entry.getValue().toString());
        }
    }
}
