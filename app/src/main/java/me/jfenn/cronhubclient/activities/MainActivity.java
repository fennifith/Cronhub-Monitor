package me.jfenn.cronhubclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.adapters.ItemAdapter;
import me.jfenn.cronhubclient.data.item.Item;
import me.jfenn.cronhubclient.data.item.MonitorItem;
import me.jfenn.cronhubclient.data.request.MonitorListRequest;
import me.jfenn.cronhubclient.data.request.Request;
import me.jfenn.cronhubclient.data.request.cronhub.Monitor;

public class MainActivity extends AppCompatActivity implements Request.OnInitListener {

    private RecyclerView recyclerView;

    private CronHub cronHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cronHub = (CronHub) getApplicationContext();

        recyclerView = findViewById(R.id.recycler);

        MonitorListRequest request = new MonitorListRequest();
        request.addOnInitListener(this);
        cronHub.addRequest(request);
    }

    @Override
    public void onInit(Request data) {
        if (data instanceof MonitorListRequest) {
            List<Item> items = new ArrayList<>();
            for (Monitor monitor : ((MonitorListRequest) data).getMonitors())
                items.add(new MonitorItem(monitor));

            recyclerView.setAdapter(new ItemAdapter(items));
        }
    }

    @Override
    public void onFailure(Request data, String message) {
        finish();
    }
}
