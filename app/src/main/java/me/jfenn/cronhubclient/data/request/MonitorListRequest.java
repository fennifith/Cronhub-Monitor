package me.jfenn.cronhubclient.data.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonitorListRequest extends Request {

    public boolean success;
    public MonitorRequest[] response;

    public MonitorListRequest() {
        super("https://cronhub.io/api/v1/monitors");
    }

    public List<MonitorRequest> getMonitors() {
        return new ArrayList<>(Arrays.asList(response));
    }

}
