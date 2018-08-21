package me.jfenn.cronhubclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.request.MonitorListRequest;
import me.jfenn.cronhubclient.data.request.RequestData;

public class MainActivity extends AppCompatActivity implements RequestData.OnInitListener {

    private CronHub cronHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cronHub = (CronHub) getApplicationContext();

        MonitorListRequest request = new MonitorListRequest();
        request.addOnInitListener(this);
        cronHub.addRequest(request);
    }

    @Override
    public void onInit(RequestData data) {

    }

    @Override
    public void onFailure(RequestData data) {
        finish();
    }
}
