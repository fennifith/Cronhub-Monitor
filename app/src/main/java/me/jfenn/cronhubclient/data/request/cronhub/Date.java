package me.jfenn.cronhubclient.data.request.cronhub;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Date {

    public String date;
    public int timezone_type;
    public String timezone;

    public java.util.Date getDate() {
        if (date == null || date.length() == 0)
            return null;

        java.util.Date obj = null;

        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone(timezone));
            obj = format.parse(date.substring(0, date.length() - 7));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

}
