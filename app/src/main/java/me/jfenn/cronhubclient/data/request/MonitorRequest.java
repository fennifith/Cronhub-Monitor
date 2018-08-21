package me.jfenn.cronhubclient.data.request;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

import me.jfenn.cronhubclient.data.request.cronhub.Date;

public class MonitorRequest extends Request {

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

    MonitorRequest(String code) {
        super("https://cronhub.io/api/v1/monitors/" + code);
    }

    public Cron getSchedule() {
        return new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)).parse(schedule);
    }

}
