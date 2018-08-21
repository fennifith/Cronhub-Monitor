package me.jfenn.cronhubclient;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.cronutils.model.time.ExecutionTime;
import com.google.common.base.Optional;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.ZonedDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.jfenn.cronhubclient.activities.SplashActivity;
import me.jfenn.cronhubclient.data.PreferenceData;
import me.jfenn.cronhubclient.data.request.MonitorListRequest;
import me.jfenn.cronhubclient.data.request.MonitorRequest;
import me.jfenn.cronhubclient.data.request.Request;
import me.jfenn.cronhubclient.data.request.cronhub.Monitor;
import me.jfenn.cronhubclient.receivers.MonitorReceiver;

public class CronHub extends Application implements Request.OnInitListener {

    private Map<Request, Long> requests;

    @Override
    public void onCreate() {
        super.onCreate();
        requests = new HashMap<>();
        AndroidThreeTen.init(this);
    }

    public void addRequest(Request request) {
        addRequest(request, PreferenceData.API_KEY.getValue(this));
    }

    public void addRequest(Request request, String key) {
        if (!(request instanceof MonitorRequest) && requests.containsKey(request) && System.currentTimeMillis() - requests.get(request) < 300000) {
            Set<Request> set = requests.keySet();
            for (Request r : set) {
                if (r.equals(request)) {
                    r.merge(request);
                    return;
                }
            }
        } else {
            requests.put(request, System.currentTimeMillis());
            request.addOnInitListener(this);
            request.startInit(this, key);
        }
    }

    public void onNotificationsChanged(Monitor monitor) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager != null) {
            if (PreferenceData.CRON_NOTIFY_FAIL.getSpecificValue(this, monitor.code)) {
                Optional<ZonedDateTime> nextTime = ExecutionTime.forCron(monitor.getSchedule()).nextExecution(ZonedDateTime.now());
                if (nextTime.isPresent()) {
                    long millis = nextTime.get().toInstant().getEpochSecond() + TimeUnit.MINUTES.toMillis(monitor.grace_period) + 30000;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        manager.setExact(AlarmManager.RTC_WAKEUP, millis, getNotificationIntent(monitor));
                    else
                        manager.set(AlarmManager.RTC_WAKEUP, millis, getNotificationIntent(monitor));

                }
            } else manager.cancel(getNotificationIntent(monitor));
        }
    }

    public PendingIntent getNotificationIntent(Monitor monitor) {
        Intent intent = new Intent(this, MonitorReceiver.class);
        intent.putExtra(MonitorReceiver.EXTRA_MONITOR_CODE, monitor.code);
        return PendingIntent.getBroadcast(this, monitor.code.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onInit(Request data) {
        if (data instanceof MonitorListRequest) {
            for (Monitor monitor : ((MonitorListRequest) data).getMonitors())
                onNotificationsChanged(monitor);
        } else if (data instanceof MonitorRequest) {
            Monitor monitor = ((MonitorRequest) data).response;
            onNotificationsChanged(monitor);
            if (!monitor.status.equals("up") || (Boolean) PreferenceData.CRON_NOTIFY_RUN.getSpecificValue(this, monitor.code)) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    NotificationCompat.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        manager.createNotificationChannel(new NotificationChannel("cronJob", getString(R.string.title_notification_channel), NotificationManager.IMPORTANCE_DEFAULT));

                        builder = new NotificationCompat.Builder(this, "cronJob");
                    } else builder = new NotificationCompat.Builder(this);

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    TimeZone time = TimeZone.getDefault();
                    format.setTimeZone(time);

                    manager.notify((int) (Math.random() * 100), builder.setContentTitle(String.format(getString(monitor.status.equals("up") ? R.string.format_cron_success : R.string.format_cron_failure), monitor.name))
                            .setContentText(String.format(getString(monitor.status.equals("up") ? R.string.format_cron_success_msg : R.string.format_cron_failure_msg), monitor.name, monitor.status.equals("up")
                                    ? String.format("%s %s", format.format(monitor.last_ping.getDate()), time.getDisplayName(false, TimeZone.SHORT))
                                    : String.valueOf(monitor.grace_period)))
                            .setSmallIcon(monitor.status.equals("up") ? R.drawable.ic_check : R.drawable.ic_error)
                            .setPriority(monitor.status.equals("up") ? NotificationCompat.PRIORITY_LOW : NotificationCompat.PRIORITY_MAX)
                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), 0))
                            .build());
                }
            }
        }
    }

    @Override
    public void onFailure(Request data, String message) {
    }
}
