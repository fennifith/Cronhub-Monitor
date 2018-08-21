package me.jfenn.cronhubclient.data.item;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cronutils.model.time.ExecutionTime;
import com.google.common.base.Optional;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.request.MonitorRequest;

public class MonitorItem extends Item<MonitorItem.ViewHolder> {

    private MonitorRequest monitor;

    public MonitorItem(MonitorRequest monitor) {
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
        holder.pingTime.setText(String.format("%s %s", format.format(monitor.last_ping.getDate()), time.getDisplayName(false, TimeZone.SHORT)));

        Optional<ZonedDateTime> nextTime = ExecutionTime.forCron(monitor.getSchedule()).nextExecution(ZonedDateTime.now());
        if (nextTime.isPresent())
            holder.nextRunTime.setText(String.format("%s %s", nextTime.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())), time.getDisplayName(false, TimeZone.SHORT)));
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
