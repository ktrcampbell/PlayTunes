package com.bigbang.playtunes.util;

import android.util.Log;

import static com.bigbang.playtunes.util.Constants.*;

public class DebugLogger {

    public static void logDebug(String tag, String message){
        Log.d(TAG, message);
    }

    public static void logError(Exception e){
    Log.d(TAG, ERROR_PREFIX + e.getLocalizedMessage());
    }
}
