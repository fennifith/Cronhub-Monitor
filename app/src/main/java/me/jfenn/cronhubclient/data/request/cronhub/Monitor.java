package me.jfenn.cronhubclient.data.request.cronhub;

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

}
