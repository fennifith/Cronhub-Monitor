package me.jfenn.cronhubclient.data.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.PreferenceData;
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
    public void bind(final ViewHolder holder) {
        Context context = holder.itemView.getContext();
        holder.title.setText(monitor.name);

        holder.status.setText(String.format(context.getString(R.string.format_status), context.getString(monitor.status.equals("up") ? R.string.title_status_up : R.string.title_status_down)));

        Drawable background = DrawableCompat.wrap(holder.status.getBackground());
        DrawableCompat.setTint(background, ContextCompat.getColor(context, monitor.status.equals("up") ? R.color.colorPositive : R.color.colorNegative));
        holder.status.setBackground(background);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        TimeZone time = TimeZone.getDefault();
        format.setTimeZone(time);
        if (monitor.last_ping != null)
            holder.pingTime.setText(String.format("%s %s", format.format(monitor.last_ping.getDate()), time.getDisplayName(false, TimeZone.SHORT)));
        else holder.pingTime.setText(R.string.error_none);

        Optional<ZonedDateTime> nextTime = ExecutionTime.forCron(monitor.getSchedule()).nextExecution(ZonedDateTime.now());
        if (nextTime.isPresent())
            holder.nextRunTime.setText(String.format("%s %s", nextTime.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())), time.getDisplayName(false, TimeZone.SHORT)));

        holder.notifications.setOnCheckedChangeListener(null);
        holder.notifications.setChecked(PreferenceData.CRON_NOTIFY_FAIL.getSpecificValue(context, monitor.code));
        holder.notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceData.CRON_NOTIFY_FAIL.setValue(buttonView.getContext(), isChecked, monitor.code);
            ((CronHub) buttonView.getContext().getApplicationContext()).onNotificationsChanged(monitor);
            holder.successNotifications.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        holder.successNotifications.setVisibility(holder.notifications.isChecked() ? View.VISIBLE : View.GONE);
        holder.successNotifications.setOnCheckedChangeListener(null);
        holder.successNotifications.setChecked(PreferenceData.CRON_NOTIFY_RUN.getSpecificValue(context, monitor.code));
        holder.successNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> PreferenceData.CRON_NOTIFY_RUN.setValue(buttonView.getContext(), isChecked, monitor.code));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView status;
        private TextView pingTime;
        private TextView nextRunTime;
        private SwitchCompat notifications;
        private AppCompatCheckBox successNotifications;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            status = v.findViewById(R.id.status);
            pingTime = v.findViewById(R.id.pingTime);
            nextRunTime = v.findViewById(R.id.nextRunTime);
            notifications = v.findViewById(R.id.notifications);
            successNotifications = v.findViewById(R.id.successNotifications);
        }
    }

}
