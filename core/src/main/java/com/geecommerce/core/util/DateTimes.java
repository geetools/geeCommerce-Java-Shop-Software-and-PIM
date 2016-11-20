package com.geecommerce.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimes {
    public static final Date newDate() {
        Date local = new Date();
        DateTimeZone zone = DateTimeZone.getDefault();
        long utc = zone.convertLocalToUTC(local.getTime(), true);

        return new Date(utc);
    }

    public static final Date newDate(long time) {
        DateTimeZone zone = DateTimeZone.getDefault();
        long utc = zone.convertLocalToUTC(time, true);

        return new Date(utc);
    }

    public static final Date newMidnightDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        DateTimeZone zone = DateTimeZone.getDefault();
        long utc = zone.convertLocalToUTC(cal.getTimeInMillis(), true);

        return new Date(utc);
    }

    public static final boolean isUTC(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        TimeZone tz = c.getTimeZone();

        return "UTC".equals(tz.getID());
    }

    public static final Date parseDate(String date) {
        DateTime dt = ISODateTimeFormat.dateTimeParser().parseDateTime(date);

        DateTimeZone zone = DateTimeZone.getDefault();
        long utc = zone.convertLocalToUTC(dt.getMillis(), true);

        return new Date(utc);
    }

    public static final Date toLocalTime(Date date, DateTimeZone dateTimeZone) {
        return new Date(dateTimeZone.convertUTCToLocal(date.getTime()));
    }

    public static final Date toUTC(Date date) {
        return newDate(date.getTime());
    }

    public static Date minOfDates(Date date1, Date date2) {
        if (date1 == null)
            return date2;

        if (date2 == null)
            return date1;

        if (date1.before(date2))
            return date1;
        else
            return date2;
    }

    public static Date maxOfDates(Date date1, Date date2) {
        if (date1 == null)
            return date2;

        if (date2 == null)
            return date1;

        if (date1.after(date2))
            return date1;
        else
            return date2;
    }
}
