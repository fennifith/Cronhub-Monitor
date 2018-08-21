package me.jfenn.cronhubclient.data.item;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

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

        holder.status.setText(String.format(holder.itemView.getContext().getString(R.string.format_status),
                holder.itemView.getContext().getString(monitor.status.equals("up") ? R.string.title_status_up : R.string.title_status_down)));

        Drawable background = DrawableCompat.wrap(holder.status.getBackground());
        DrawableCompat.setTint(background, ContextCompat.getColor(holder.itemView.getContext(), monitor.status.equals("up") ? R.color.colorPositive : R.color.colorNegative));
        holder.status.setBackground(background);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        TimeZone time = TimeZone.getDefault();
        format.setTimeZone(time);
        holder.pingTime.setText(format.format(monitor.last_ping.getDate()) + " " + time.getDisplayName(false, TimeZone.SHORT));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView status;
        private TextView pingTime;
        private TextView nextRunTime;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            status = v.findViewById(R.id.status);
            pingTime = v.findViewById(R.id.pingTime);
            nextRunTime = v.findViewById(R.id.nextRunTime);
        }
    }

}