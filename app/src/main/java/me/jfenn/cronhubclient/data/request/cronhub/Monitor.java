package me.jfenn.cronhubclient.data.request.cronhub;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import java.util.Date;

public class Monitor {

    public String name;
    public String code;
    public String schedule;
    public int grace_period;
    public String timezone;
    public String status;
    public Date last_ping;
    public int running_time;
    public String running_time_unit;
    public Date created_at;

    public Cron getSchedule() {
        if (schedule != null)
            return new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)).parse(schedule);
        else return null;
    }

}
