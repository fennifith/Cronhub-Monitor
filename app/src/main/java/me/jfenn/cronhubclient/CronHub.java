package me.jfenn.cronhubclient;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.jfenn.cronhubclient.data.PreferenceData;
import me.jfenn.cronhubclient.data.request.Request;

public class CronHub extends Application {

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
        if (requests.containsKey(request) && System.currentTimeMillis() - requests.get(request) < 300000) {
            Set<Request> set = requests.keySet();
            for (Request r : set) {
                if (r.equals(request)) {
                    r.merge(request);
                    return;
                }
            }
        } else {
            requests.put(request, System.currentTimeMillis());
            request.startInit(this, key);
        }
    }
}
