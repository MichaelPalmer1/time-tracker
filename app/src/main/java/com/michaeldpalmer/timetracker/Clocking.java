package com.michaeldpalmer.timetracker;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

public class Clocking {

    private int _id;
    private int _period;
    private int _type;
    private int _week;
    private int _day;
    private String _date;
    private boolean _lunch;

    public static final int CLOCK_IN = 0;
    public static final int CLOCK_OUT = 1;

    public static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public Clocking() {

    }

    public Clocking(int id, int type, int period, int week, int day, String date, boolean lunch) {
        this._id = id;
        this._type = type;
        this._period = period;
        this._week = week;
        this._day = day;
        this._date = date;
        this._lunch = lunch;
    }

    public Clocking(int type, int period, int week, int day, String date, boolean lunch) {
        this._type = type;
        this._period = period;
        this._week = week;
        this._day = day;
        this._date = date;
        this._lunch = lunch;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getType() {
        return this._type;
    }

    public void setType(int type) {
        this._type = type;
    }

    public int getPeriod() {
        return this._period;
    }

    public void setPeriod(int period) {
        this._period = period;
    }

    public int getWeek() {
        return _week;
    }

    public void setWeek(int week) {
        this._week = week;
    }

    public int getDay() {
        return _day;
    }

    public void setDay(int day) {
        this._day = day;
    }

    public String getDate() {
        return _date;
    }

    public void setDate(String date) {
        this._date = date;
    }

    public boolean getLunch() {
        return this._lunch;
    }

    public void setLunch(boolean lunch) {
        this._lunch = lunch;
    }

    public static int calculatePeriod(Date date) {
        int period = 1;

        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        Calendar january = Calendar.getInstance();
        january.set( january.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);

        LocalDate localDate = LocalDate.fromDateFields(january.getTime());
        LocalDate monday = localDate.withDayOfWeek(DateTimeConstants.MONDAY);

        if( monday.getMonthOfYear() == 12)
            monday = monday.plusWeeks(1);

        LocalDate before = monday.minusDays(1); // Sunday
        LocalDate after = before.plusWeeks(1).plusDays(6); // Saturday

        while( true ) {
            int beforeCompare = before.toDate().compareTo(date);
            int afterCompare = after.toDate().compareTo(date);

            if ( beforeCompare == -1 && afterCompare == -1 ) {
                Log.i("DateFinder", "Date is before the period");
                period++;
                before = before.plusWeeks(2);
                after = after.plusWeeks(2);
            } else if ( beforeCompare >= 0 && afterCompare <= 0 ) {
                Log.i("DateFinder", "Date is in the period");
                break;
            } else {
                Log.i("DateFinder", "Something else " + beforeCompare + " " + afterCompare);
                break;
            }
        }

        if( Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )
            period--;

        Log.i("Period", String.valueOf(period));

        return period;
    }
}
