package com.michaeldpalmer.timetracker;

import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Calendar;
import java.util.List;

public class OverviewFragment extends Fragment implements CalendarView.OnDateChangeListener {

    private DatabaseHandler db;
    private TextView txtMonday1, txtTuesday1, txtWednesday1, txtThursday1, txtFriday1, txtTotal1;
    private TextView txtMonday2, txtTuesday2, txtWednesday2, txtThursday2, txtFriday2, txtTotal2;
    private TextView txtData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        DatabaseHandler db = new DatabaseHandler(getActivity());
        db.getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        txtMonday1 = (TextView) rootView.findViewById(R.id.hoursMonday1);
        txtTuesday1 = (TextView) rootView.findViewById(R.id.hoursTuesday1);
        txtWednesday1 = (TextView) rootView.findViewById(R.id.hoursWednesday1);
        txtThursday1 = (TextView) rootView.findViewById(R.id.hoursThursday1);
        txtFriday1 = (TextView) rootView.findViewById(R.id.hoursFriday1);
        txtTotal1 = (TextView) rootView.findViewById(R.id.hoursTotal1);

        txtMonday2 = (TextView) rootView.findViewById(R.id.hoursMonday2);
        txtTuesday2 = (TextView) rootView.findViewById(R.id.hoursTuesday2);
        txtWednesday2 = (TextView) rootView.findViewById(R.id.hoursWednesday2);
        txtThursday2 = (TextView) rootView.findViewById(R.id.hoursThursday2);
        txtFriday2 = (TextView) rootView.findViewById(R.id.hoursFriday2);
        txtTotal2 = (TextView) rootView.findViewById(R.id.hoursTotal2);
        txtData = (TextView) rootView.findViewById(R.id.txtData);

        db = new DatabaseHandler(getActivity());

        Log.i("PayPeriod", String.valueOf(MainActivity.PAY_PERIOD));

        DateTime weekStart = new DateTime()
                .withWeekOfWeekyear( (MainActivity.PAY_PERIOD * 2) - 1)
                .withDayOfWeek(DateTimeConstants.SUNDAY)
                .withTimeAtStartOfDay();

        Log.i("WeekStart", weekStart.toString());

        DateTime weekEnd = new DateTime()
                .withWeekOfWeekyear((MainActivity.PAY_PERIOD * 2) + 1)
                .withDayOfWeek(DateTimeConstants.SATURDAY)
                .withTimeAtStartOfDay();

        Log.i("WeekEnd", weekEnd.toString());

        calendarView.setMinDate(weekStart.toDate().getTime());
        calendarView.setMaxDate(weekEnd.toDate().getTime());
        calendarView.setOnDateChangeListener(this);

        refreshData();

        return rootView;
    }

    public void refreshData() {
        List<Clocking> clockings = db.getAllClockings();

        String data = "";
        for ( int i = clockings.size() - 1; i >= 0; i-- ) {
            Clocking clocking = clockings.get(i);
            data += String.format("%s\t\t(PP %d) - %s\n",
                    clocking.getType() == Clocking.CLOCK_IN ? "IN  " : "OUT",
                    clocking.getPeriod(),
                    clocking.getDate()
            );
        }

        txtData.setText(data.trim());

        double monday1Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(1, Calendar.MONDAY);
        monday1Time += monday1Time < 0 ? (40.0/60.0) : 0;
        double tuesday1Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(1, Calendar.TUESDAY);
        tuesday1Time += tuesday1Time < 0 ? (40.0/60.0) : 0;
        double wednesday1Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(1, Calendar.WEDNESDAY);
        wednesday1Time += wednesday1Time < 0 ? (40.0/60.0) : 0;
        double thursday1Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(1, Calendar.THURSDAY);
        thursday1Time += thursday1Time < 0 ? (40.0/60.0) : 0;
        double friday1Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(1, Calendar.FRIDAY);
        friday1Time += friday1Time < 0 ? (40.0/60.0) : 0;
        double total1Time = db.getPayPeriod(MainActivity.PAY_PERIOD).getTotal(1);
        total1Time += total1Time < 0 ? (40.0/60.0) : 0;
        txtMonday1.setText(String.format("%1.2f", monday1Time ) );
        txtTuesday1.setText( String.format("%1.2f", tuesday1Time ) );
        txtWednesday1.setText( String.format("%1.2f", wednesday1Time ) );
        txtThursday1.setText(String.format("%1.2f", thursday1Time ) );
        txtFriday1.setText(String.format("%1.2f", friday1Time ) );
        txtTotal1.setText(String.format("%1.2f", total1Time ) );

        double monday2Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(2, Calendar.MONDAY);
        monday2Time += monday2Time < 0 ? (40.0/60.0) : 0;
        double tuesday2Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(2, Calendar.TUESDAY);
        tuesday2Time += tuesday2Time < 0 ? (40.0/60.0) : 0;
        double wednesday2Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(2, Calendar.WEDNESDAY);
        wednesday2Time += wednesday2Time < 0 ? (40.0/60.0) : 0;
        double thursday2Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(2, Calendar.THURSDAY);
        thursday2Time += thursday2Time < 0 ? (40.0/60.0) : 0;
        double friday2Time = db.getPayPeriod(MainActivity.PAY_PERIOD).get(2, Calendar.FRIDAY);
        friday2Time += friday2Time < 0 ? (40.0/60.0) : 0;
        double total2Time = db.getPayPeriod(MainActivity.PAY_PERIOD).getTotal(2);
        total2Time += total2Time < 0 ? (40.0/60.0) : 0;
        txtMonday2.setText(String.format("%1.2f", monday2Time ) );
        txtTuesday2.setText( String.format("%1.2f", tuesday2Time ) );
        txtWednesday2.setText( String.format("%1.2f", wednesday2Time ) );
        txtThursday2.setText(String.format("%1.2f", thursday2Time ) );
        txtFriday2.setText(String.format("%1.2f", friday2Time ) );
        txtTotal2.setText(String.format("%1.2f", total2Time ) );
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        Log.i("DateChange", String.format("%d-%02d-%02d", year, month, dayOfMonth));

        DialogFragment df = new ClockingDialog();
//        DialogFragment df = new HolidayDialog();

        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", dayOfMonth);
        df.setArguments(args);

        df.show(getActivity().getSupportFragmentManager(), "clockingDialog");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.action_refresh ) {
            refreshData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}