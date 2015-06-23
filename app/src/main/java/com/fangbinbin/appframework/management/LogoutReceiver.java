package com.fangbinbin.appframework.management;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * LogoutReceiver
 *
 * Receives logout broadcast and finishes activity.
 */

public class LogoutReceiver extends BroadcastReceiver {

    public static final String LOGOUT = "finish_all_activities";

    @Override
    public void onReceive(Context context, Intent intent) {
        ((Activity) context).finish();
    }
}
