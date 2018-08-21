package me.jfenn.cronhubclient.data.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.request.cronhub.Monitor;

public class MonitorItem extends Item<MonitorItem.ViewHolder> {

    private Monitor monitor;

    public MonitorItem(Monitor monitor) {
        super(R.layout.item_monitor);
        this.monitor = monitor;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(ViewHolder holder) {
        holder.title.setText(monitor.name);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
        }
    }

}
