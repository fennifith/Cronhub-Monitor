package me.jfenn.cronhubclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.PreferenceData;
import me.jfenn.cronhubclient.data.request.MonitorListRequest;
import me.jfenn.cronhubclient.data.request.RequestData;

public class SplashActivity extends AppCompatActivity implements RequestData.OnInitListener {

    private CronHub cronHub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cronHub = (CronHub) getApplicationContext();

        String key = PreferenceData.API_KEY.getValue(this);
        if (key != null)
            startRequest();
        else {
            findViewById(R.id.signin).setVisibility(View.VISIBLE);

        }
    }

    private void startRequest() {
        MonitorListRequest request = new MonitorListRequest();
        request.addOnInitListener(this);
        cronHub.addRequest(request);
    }

    @Override
    public void onInit(RequestData data) {
        if (data instanceof MonitorListRequest) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onFailure(RequestData data) {
        findViewById(R.id.signin).setVisibility(View.VISIBLE);
        Toast.makeText(this, "Error: authentication failed", Toast.LENGTH_SHORT).show();
    }
}
