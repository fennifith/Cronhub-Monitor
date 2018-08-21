package me.jfenn.cronhubclient.data.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jfenn.cronhubclient.data.request.cronhub.Monitor;

public class MonitorListRequest extends Request {

    public boolean success;
    public Monitor[] response;

    public MonitorListRequest() {
        super("https://cronhub.io/api/v1/monitors");
    }

    public List<Monitor> getMonitors() {
        return new ArrayList<>(Arrays.asList(response));
    }

}
