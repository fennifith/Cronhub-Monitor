package me.jfenn.cronhubclient.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.data.request.MonitorRequest;

public class MonitorReceiver extends BroadcastReceiver {

    public static final String EXTRA_MONITOR_CODE = "me.jfenn.cronhubclient.receivers.MonitorReceiver.EXTRA_MONITOR_CODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String code = intent.getStringExtra(EXTRA_MONITOR_CODE);
        if (code != null)
            ((CronHub) context.getApplicationContext()).addRequest(new MonitorRequest(code));
    }

}
