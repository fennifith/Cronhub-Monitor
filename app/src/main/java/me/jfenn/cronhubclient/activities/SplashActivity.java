package me.jfenn.cronhubclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.PreferenceData;
import me.jfenn.cronhubclient.data.request.MonitorListRequest;
import me.jfenn.cronhubclient.data.request.RequestData;

public class SplashActivity extends AppCompatActivity implements RequestData.OnInitListener {

    private CronHub cronHub;

    private View signInView;
    private EditText apiKeyView;
    private View signInButtonView;

    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cronHub = (CronHub) getApplicationContext();

        signInView = findViewById(R.id.signin);
        apiKeyView = findViewById(R.id.apiKey);
        signInButtonView = findViewById(R.id.signInButton);

        key = PreferenceData.API_KEY.getValue(this);
        if (key != null && key.length() > 0)
            startRequest();
        else signInView.setVisibility(View.VISIBLE);

        signInButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apiKeyView.getText().toString().length() > 0) {
                    signInView.setVisibility(View.GONE);
                    key = apiKeyView.getText().toString();
                    startRequest();
                } else Toast.makeText(SplashActivity.this, "Error: missing API key", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startRequest() {
        MonitorListRequest request = new MonitorListRequest();
        request.addOnInitListener(this);
        cronHub.addRequest(request, key);
    }

    @Override
    public void onInit(RequestData data) {
        if (data instanceof MonitorListRequest) {
            PreferenceData.API_KEY.setValue(SplashActivity.this, key);
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    @Override
    public void onFailure(RequestData data, String message) {
        findViewById(R.id.signin).setVisibility(View.VISIBLE);
        Toast.makeText(this, "Authentication failed: " + message, Toast.LENGTH_SHORT).show();
    }
}
