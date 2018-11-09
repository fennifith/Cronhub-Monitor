package me.jfenn.cronhubclient.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.jfenn.attribouter.Attribouter;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about)
            Attribouter.from(this).show();

        return super.onOptionsItemSelected(item);
    }
}
