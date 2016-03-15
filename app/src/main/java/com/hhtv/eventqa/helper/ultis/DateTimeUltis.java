package com.hhtv.eventqa.helper.ultis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import hugo.weaving.DebugLog;

/**
 * Created by nienb on 4/3/16.
 */
public class DateTimeUltis {

    @DebugLog
    public static String getLastCheck(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        String s = spfr.getString("lastcheck", DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss"));
        Log.d("MYTAG","get: " +s);
        setLastCheck(mContext);
        return s;
    }
    @DebugLog
    public static void setLastCheck(Context mContext) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        String s = DateTime.now(DateTimeZone.UTC).toString("MM-dd-yyyy HH:mm:ss");
        Log.d("MYTAG","set: " +s);
        editor.putString("lastcheck", s);
        editor.commit();
    }
}
