package me.jfenn.cronhubclient;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.jfenn.cronhubclient.data.PreferenceData;
import me.jfenn.cronhubclient.data.request.RequestData;

public class CronHub extends Application {

    private Map<RequestData, Long> requests;

    @Override
    public void onCreate() {
        super.onCreate();
        requests = new HashMap<>();
    }

    public void addRequest(RequestData request) {
        addRequest(request, (String) PreferenceData.API_KEY.getValue(this));
    }

    public void addRequest(RequestData request, String key) {
        if (requests.containsKey(request) && System.currentTimeMillis() - requests.get(request) < 300000) {
            Set<RequestData> set = requests.keySet();
            for (RequestData r : set) {
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
