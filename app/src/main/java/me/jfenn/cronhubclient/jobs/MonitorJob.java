package me.jfenn.cronhubclient.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import androidx.annotation.NonNull;
import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.data.request.MonitorRequest;
import me.jfenn.cronhubclient.data.request.Request;

public class MonitorJob extends Job implements Request.OnInitListener {

    private Result state;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        String monitor = params.getTag();
        if (monitor != null) {
            MonitorRequest request = new MonitorRequest(monitor);
            request.addOnInitListener(this);
            ((CronHub) getContext().getApplicationContext()).addRequest(request);

            while (state == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    return Result.FAILURE;
                }
            }

            return state;
        }

        return Result.FAILURE;
    }

    @Override
    public void onInit(Request data) {
        state = Result.SUCCESS;
    }

    @Override
    public void onFailure(Request data, String message) {
        state = Result.FAILURE;
    }

    public static void scheduleJob(String monitor, long triggerTime) {
        new JobRequest.Builder(monitor)
                .setExact(triggerTime)
                .build()
                .schedule();
    }
}
