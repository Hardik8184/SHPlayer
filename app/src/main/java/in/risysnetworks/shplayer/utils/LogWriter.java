package in.risysnetworks.shplayer.utils;

import android.util.Log;


public class LogWriter {

    public static void debug(String TAG, String writeText) {
        Log.d(TAG, writeText);
    }

    public static void info(String TAG, String writeText) {
        Log.i(TAG, writeText);
    }
}
