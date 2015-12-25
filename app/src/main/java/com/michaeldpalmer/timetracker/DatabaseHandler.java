package com.michaeldpalmer.timetracker;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "timeManager";
    private static final String TABLE_CLOCKINGS = "clockings";

    // Columns
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PERIOD = "period";
    private static final String KEY_WEEK_OF_YEAR = "week_of_year";
    private static final String KEY_DAY_OF_WEEK = "day_of_week";
    private static final String KEY_DATE = "date";
    private static final String KEY_LUNCH = "lunch";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLOCKINGS_TABLE = "CREATE TABLE " + TABLE_CLOCKINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TYPE + " INTEGER,"
                + KEY_PERIOD + " INTEGER,"
                + KEY_WEEK_OF_YEAR + " INTEGER,"
                + KEY_DAY_OF_WEEK + " INTEGER,"
                + KEY_DATE + " DATETIME,"
                + KEY_LUNCH + " BOOLEAN)";

        db.execSQL(CREATE_CLOCKINGS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOCKINGS);
        onCreate(db);
    }

    public void addClocking(Clocking clocking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, clocking.getType());
        values.put(KEY_PERIOD, clocking.getPeriod());
        values.put(KEY_WEEK_OF_YEAR, clocking.getWeek());
        values.put(KEY_DAY_OF_WEEK, clocking.getDay());
        values.put(KEY_DATE, clocking.getDate());
        values.put(KEY_LUNCH, clocking.getLunch());

        db.insert(TABLE_CLOCKINGS, null, values);
        db.close();

        String log = String.format("Added clocking: %s - Pay Period %d - %s",
                clocking.getType() == Clocking.CLOCK_IN ? "IN" : "OUT",
                clocking.getPeriod(),
                clocking.getDate());

        Log.i("Clocking", log);
    }

    public Clocking getClocking(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CLOCKINGS,
                new String[]{KEY_ID, KEY_TYPE, KEY_PERIOD, KEY_WEEK_OF_YEAR, KEY_DAY_OF_WEEK, KEY_DATE, KEY_LUNCH}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );

        if ( cursor != null )
            cursor.moveToFirst();

        Clocking clocking = new Clocking(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getString(5),
                cursor.getInt(6) == 1
        );

        cursor.close();
        return clocking;
    }

    public List<Clocking> getAllClockings() {
        List<Clocking> clockingList = new ArrayList<Clocking>();
        String selectQuery = "SELECT  * FROM " + TABLE_CLOCKINGS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Clocking clocking = new Clocking();
                clocking.setID(cursor.getInt(0));
                clocking.setType(cursor.getInt(1));
                clocking.setPeriod(cursor.getInt(2));
                clocking.setWeek(cursor.getInt(3));
                clocking.setDay(cursor.getInt(4));
                clocking.setDate(cursor.getString(5));
                clocking.setLunch(cursor.getInt(6) == 1);
                clockingList.add(clocking);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return clockingList;
    }

    public void recreateDatabase() {
        this.onUpgrade(this.getWritableDatabase(), 0, 1);
    }

    public void truncateDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CLOCKINGS);
        db.close();
    }

    public int updateClocking(Clocking clocking) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, clocking.getType());
        values.put(KEY_PERIOD, clocking.getPeriod());
        values.put(KEY_DATE, clocking.getDate());
        values.put(KEY_DAY_OF_WEEK, clocking.getDay());
        values.put(KEY_WEEK_OF_YEAR, clocking.getWeek());
        values.put(KEY_LUNCH, clocking.getLunch());

        return db.update(TABLE_CLOCKINGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(clocking.getID()) });
    }

    public void deleteClocking(Clocking clocking) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLOCKINGS, KEY_ID + " = ?",
                new String[]{String.valueOf(clocking.getID())});
        db.close();
    }

    public void deleteClocking(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( TABLE_CLOCKINGS, KEY_ID + " = ?", new String[] { String.valueOf(id) } );
        db.close();
    }

    public void add(String type, String period, String week_of_year, String day_of_week, String date, boolean lunch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_PERIOD, period);
        values.put(KEY_WEEK_OF_YEAR, week_of_year);
        values.put(KEY_DAY_OF_WEEK, day_of_week);
        values.put(KEY_DATE, date);
        values.put(KEY_LUNCH, lunch);
        db.insert(TABLE_CLOCKINGS, null, values);
        db.close();
    }

    public void getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CLOCKINGS,
                new String[] { KEY_ID, KEY_TYPE, KEY_PERIOD, KEY_WEEK_OF_YEAR, KEY_DAY_OF_WEEK, KEY_DATE, KEY_LUNCH },
                null,
                null,
                null, null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                Log.i("AllDB",
                        String.format("ID: %d, Type: %s, Period: %d, Week: %d, Day: %d, Date: %s, Lunch: %s",
                                cursor.getInt(0),
                                cursor.getInt(1) == Clocking.CLOCK_IN ? "IN" : "OUT",
                                cursor.getInt(2),
                                cursor.getInt(3),
                                cursor.getInt(4),
                                cursor.getString(5),
                                cursor.getInt(6) == 1
                        )
                );
            } while (cursor.moveToNext());
        } else {
            Log.i("AllDB", "DB is empty");
        }

        cursor.close();
        db.close();
    }

    public Clocking[] getDay(LocalDate date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CLOCKINGS,
                new String[]{KEY_ID, KEY_TYPE, KEY_PERIOD, KEY_WEEK_OF_YEAR, KEY_DAY_OF_WEEK, KEY_DATE, KEY_LUNCH},
                KEY_DATE + " between ? and ?",
                new String[]{date.toString("yyyy-MM-dd 00:00:00"), date.toString("yyyy-MM-dd 23:59:59")},
                null, null, null, null
        );

        Clocking[] clockings = new Clocking[2];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                Log.i("GetClocking",
                        String.format("ID: %d, Type: %s, Period: %d, Week: %d, Day: %d, Date: %s, Lunch: %s",
                                cursor.getInt(0),
                                cursor.getInt(1) == Clocking.CLOCK_IN ? "IN" : "OUT",
                                cursor.getInt(2),
                                cursor.getInt(3),
                                cursor.getInt(4),
                                cursor.getString(5),
                                cursor.getInt(6) == 1
                        )
                );
                clockings[i] = new Clocking(cursor.getInt(0), cursor.getInt(1),
                        cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6) == 1);
                i++;
            } while (cursor.moveToNext());
        } else {
            Log.i("GetClocking", "No clockings found");
            return null;
        }

        cursor.close();
        db.close();

        return clockings;
    }

    public PayPeriod getPayPeriod(int payPeriod) {
        boolean noClockings = false;
        HashMap<String, String> result = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CLOCKINGS,
                new String[] { KEY_TYPE, KEY_WEEK_OF_YEAR, KEY_DAY_OF_WEEK, KEY_DATE, KEY_LUNCH },
                KEY_PERIOD + " = ?",
                new String[] {
                        String.valueOf(payPeriod),
                },
                null, null, null, null
        );

        PayPeriod p = new PayPeriod();

        if (cursor.moveToFirst()) {
            do {
                int week = cursor.getInt(1) == (payPeriod * 2) ? 1 : 2;
                p.add( cursor.getInt(0), week, cursor.getInt(2), cursor.getString(3), cursor.getInt(4) );
            } while (cursor.moveToNext());
        } else {
            noClockings = true;
        }

        cursor.close();
        db.close();

        if (noClockings) {
            Log.i("GetPayPeriod", "No Clockings Found");
            return new PayPeriod();
        }

        return p;
    }

}

class PayPeriod {
    private PayWeek week1 = new PayWeek();
    private PayWeek week2 = new PayWeek();

    public void add(int type, int week, int day, String date, int lunch) {
        if( week == 1) {
            week1.add(type, day, date, lunch);
        } else {
            week2.add(type, day, date, lunch);
        }
    }

    public double get(int week, int day) {
        if(week == 1) {
            return week1.getHours(day);
        } else {
            return week2.getHours(day);
        }
    }

    public double getTotal(int week) {
        if(week == 1) {
            return week1.getTotal();
        } else {
            return week2.getTotal();
        }
    }
}

class PayWeek {
    private DateTime mondayIn = new DateTime(), mondayOut = new DateTime();
    private DateTime tuesdayIn = new DateTime(), tuesdayOut = new DateTime();
    private DateTime wednesdayIn = new DateTime(), wednesdayOut = new DateTime();
    private DateTime thursdayIn = new DateTime(), thursdayOut = new DateTime();
    private DateTime fridayIn = new DateTime(), fridayOut = new DateTime();
    private boolean mondayLunch = true;
    private boolean tuesdayLunch = true;
    private boolean wednesdayLunch = true;
    private boolean thursdayLunch = true;
    private boolean fridayLunch = true;

    private static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public void add(int type, int day, String date, int lunch) {
        switch(day) {
            case Calendar.MONDAY:
                if(type == 0)
                    mondayIn = DateTime.parse(date, format);
                else
                    mondayOut = DateTime.parse(date, format);
                mondayLunch = lunch == 1;
                break;
            case Calendar.TUESDAY:
                if(type == 0)
                    tuesdayIn = DateTime.parse(date, format);
                else
                    tuesdayOut = DateTime.parse(date, format);
                tuesdayLunch = lunch == 1;
                break;
            case Calendar.WEDNESDAY:
                if(type == 0)
                    wednesdayIn = DateTime.parse(date, format);
                else
                    wednesdayOut = DateTime.parse(date, format);
                wednesdayLunch = lunch == 1;
                break;
            case Calendar.THURSDAY:
                if(type == 0)
                    thursdayIn = DateTime.parse(date, format);
                else
                    thursdayOut = DateTime.parse(date, format);
                thursdayLunch = lunch == 1;
                break;
            case Calendar.FRIDAY:
                if(type == 0)
                    fridayIn = DateTime.parse(date, format);
                else
                    fridayOut = DateTime.parse(date, format);
                fridayLunch = lunch == 1;
                break;
        }
    }

    public double getTotal() {
        double total = 0.0;
        for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++)
            total += getHours(i);

        return total;
    }

    public double getHours(int day) {
        Period period = new Period();
        boolean lunch = true;
        switch(day) {
            case Calendar.MONDAY:
                period = new Period(mondayIn, mondayOut);
                lunch = mondayLunch;
                break;
            case Calendar.TUESDAY:
                period = new Period(tuesdayIn, tuesdayOut);
                lunch = tuesdayLunch;
                break;
            case Calendar.WEDNESDAY:
                period = new Period(wednesdayIn, wednesdayOut);
                lunch = wednesdayLunch;
                break;
            case Calendar.THURSDAY:
                period = new Period(thursdayIn, thursdayOut);
                lunch = thursdayLunch;
                break;
            case Calendar.FRIDAY:
                period = new Period(fridayIn, fridayOut);
                lunch = fridayLunch;
                break;
        }

        double duration = period.getHours() + (period.getMinutes() / 60.0);
        double lunchTime = lunch ? (40.0 / 60.0) : 0.0;

        if ( duration == 0.0 )
            return 0.0;
        else
            return duration - lunchTime;
    }
}