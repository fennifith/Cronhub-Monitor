package me.jfenn.cronhubclient.data.request;

import me.jfenn.cronhubclient.data.request.cronhub.Monitor;

public class MonitorRequest extends Request {

    public boolean success;
    public Monitor response;

    public MonitorRequest(String code) {
        super("https://cronhub.io/api/v1/monitors/" + code);
    }

}
