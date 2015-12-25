package com.michaeldpalmer.timetracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.util.Calendar;

public class ClockingDialog extends DialogFragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private TextView timeIn, timeOut, timeClocked, timePaid;
    private Switch lunch;
    private LocalTime in = new LocalTime(), out = new LocalTime();
    private static final int TIME_IN = 0, TIME_OUT = 1;
    private int timeType = TIME_IN;
    private boolean hasClockings = true;
    private Clocking[] clockings;
    private DatabaseHandler db;
    private int month, day, year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_clocking, container, false);

        ImageButton btnIn = (ImageButton) rootView.findViewById(R.id.btnEditIn);
        ImageButton btnOut = (ImageButton) rootView.findViewById(R.id.btnEditOut);
        timeIn = (TextView) rootView.findViewById(R.id.timeIn);
        timeOut = (TextView) rootView.findViewById(R.id.timeOut);
        timeClocked = (TextView) rootView.findViewById(R.id.timeClocked);
        timePaid = (TextView) rootView.findViewById(R.id.timePaid);
        lunch = (Switch) rootView.findViewById(R.id.switchLunch);
        Button holiday = (Button) rootView.findViewById(R.id.btnHoliday);
        Button vacation = (Button) rootView.findViewById(R.id.btnVacation);
        Button btnClock = (Button) rootView.findViewById(R.id.btnClock);
        TableRow rowLunch = (TableRow) rootView.findViewById(R.id.rowLunch);
        TableRow rowClocked = (TableRow) rootView.findViewById(R.id.rowClocked);
        TableRow rowPaid = (TableRow) rootView.findViewById(R.id.rowPaid);
        TableRow rowTimeIn = (TableRow) rootView.findViewById(R.id.rowTimeIn);
        TableRow rowTimeOut = (TableRow) rootView.findViewById(R.id.rowTimeOut);

        btnIn.setOnClickListener(this);
        btnOut.setOnClickListener(this);
        lunch.setOnClickListener(this);
        holiday.setOnClickListener(this);
        vacation.setOnClickListener(this);
        btnClock.setOnClickListener(this);

        month = getArguments().getInt("month") + 1;
        day = getArguments().getInt("day");
        year = getArguments().getInt("year");

        LocalDate date = LocalDate.parse(String.format("%d-%02d-%02d", year, month, day));

        db =  new DatabaseHandler(getActivity());
        clockings = db.getDay(date);

        if ( clockings == null ) {
            hasClockings = false;
            rowTimeIn.setVisibility(View.GONE);
            rowTimeOut.setVisibility(View.GONE);
            rowLunch.setVisibility(View.GONE);
            rowClocked.setVisibility(View.GONE);
            rowPaid.setVisibility(View.GONE);
            btnClock.setText("Clock In");
            btnClock.setVisibility(View.VISIBLE);
            holiday.setVisibility(View.VISIBLE);
            vacation.setVisibility(View.VISIBLE);
        } else if ( clockings[1] == null ) {
            hasClockings = true;
            out = null;
            in = LocalTime.parse(clockings[0].getDate(), Clocking.format);
            lunch.setChecked(clockings[0].getLunch());
            rowTimeOut.setVisibility(View.GONE);
            btnClock.setText("Clock Out");
            btnClock.setVisibility(View.VISIBLE);
            updateData();
        } else {
            hasClockings = true;
            for (Clocking clocking : clockings) {
                if (clocking.getType() == Clocking.CLOCK_IN) {
                    in = LocalTime.parse(clocking.getDate(), Clocking.format);
                    lunch.setChecked(clocking.getLunch());
                } else {
                    out = LocalTime.parse(clocking.getDate(), Clocking.format);
                }
            }
            updateData();
        }

        super.getDialog().setTitle(date.toString("EEE, MMM d, yyyy"));

        return rootView;
    }

    public void showTimeDialog(int type) {
        showTimeDialog(
                type,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE)
        );
    }

    public void setLunch() {
        clockings[0].setLunch( lunch.isChecked() );
        db.updateClocking(clockings[0]);
        updateData();
    }

    public void setHoliday() {
        DateTime date = new DateTime().withDate(year, month, day).withTime(8, 0, 0, 0);
        db.addClocking(
                new Clocking(
                        Clocking.CLOCK_IN,
                        MainActivity.PAY_PERIOD,
                        Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                        Clocking.format.print(date),
                        false
                )
        );
        date = new DateTime().withDate(year, month, day).withTime(16, 0, 0, 0);
        db.addClocking(
                new Clocking(
                        Clocking.CLOCK_OUT,
                        MainActivity.PAY_PERIOD,
                        Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                        Clocking.format.print(date),
                        false
                )
        );
        this.dismiss();
    }

    public void setVacation() {
        Log.i("Vacation", "Setting vacation");
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        Button hours4 = new Button(getActivity());
        Button hours8 = new Button(getActivity());

        hours4.setText("Half day");
        hours8.setText("Full day");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);

        layout.addView(hours4, params);
        layout.addView(hours8, params);
        layout.setPadding(10, 10, 10, 10);

        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Vacation");
        dialog.setContentView(layout);
        dialog.show();
    }

    public void updateData() {
        Period period;

        timeIn.setText(in.toString("h:mm a"));

        if ( out == null ) {
            period = new Period(in, new LocalTime());
        } else {
            timeOut.setText(out.toString("h:mm a"));
            period = new Period(in, out);
        }

        int clockedHours = period.getHours(), clockedMinutes = period.getMinutes();

        if (clockedMinutes < 0) {
            clockedHours--;
            clockedMinutes += 60;
        }

        double clockedTime = clockedHours + (clockedMinutes / 60.0);

        timeClocked.setText(String.format("%1.2f", clockedTime));

        Period withoutLunch = period.minusMinutes(40);
        int paidHours = withoutLunch.getHours(), paidMinutes = withoutLunch.getMinutes();

        if (paidMinutes < 0) {
            paidHours--;
            paidMinutes += 60;
        }

        double paidTime = paidHours + (paidMinutes / 60.0);

        if ( paidTime < 0 )
            paidTime = clockedTime;

        if (lunch.isChecked())
            timePaid.setText(String.format("%1.2f", paidTime));
        else
            timePaid.setText(String.format("%1.2f", clockedTime));
    }
    public void showTimeDialog(int type, int hour, int minute) {
        timeType = type;
        TimePickerDialog dialog = new TimePickerDialog(
                getActivity(), this, hour, minute, DateFormat.is24HourFormat( getActivity() )
        );
        dialog.setTitle( type == TIME_IN ? "Time In" : "Time Out" );
        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateTime date = new DateTime().withDate(year, month, day).withTime(hourOfDay, minute, 0, 0);
        String dateString = Clocking.format.print(date);

        if( timeType == TIME_IN ) {
            in = date.toLocalTime();

            if(!hasClockings) {
                db.addClocking(
                        new Clocking(
                                Clocking.CLOCK_IN,
                                MainActivity.PAY_PERIOD,
                                Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
                                Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                                Clocking.format.print(date),
                                true
                        )
                );
                this.dismiss();
            } else {
                for(Clocking clocking: clockings) {
                    if( clocking.getType() == Clocking.CLOCK_IN) {
                        clocking.setDate(dateString);
                        db.updateClocking(clocking);
                    }
                }
            }

        } else if ( timeType == TIME_OUT ) {
            out = date.toLocalTime();

            if(hasClockings) {
                if ( clockings[1] == null ) {
                    db.addClocking(
                            new Clocking(
                                    Clocking.CLOCK_OUT,
                                    MainActivity.PAY_PERIOD,
                                    Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
                                    Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                                    Clocking.format.print(date),
                                    true
                            )
                    );
                    this.dismiss();
                } else {
                    clockings[1].setDate(dateString);
                    db.updateClocking(clockings[1]);
                }
            }
        }
        updateData();
    }

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.btnEditIn:
                showTimeDialog(TIME_IN, in.getHourOfDay(), in.getMinuteOfHour());
                break;

            case R.id.btnEditOut:
                showTimeDialog(TIME_OUT, out.getHourOfDay(), out.getMinuteOfHour());
                break;

            case R.id.switchLunch:
                setLunch();
                break;

            case R.id.btnHoliday:
                setHoliday();
                break;

            case R.id.btnVacation:
                setVacation();
                break;

            case R.id.btnClock:
                if(!hasClockings)
                    showTimeDialog(TIME_IN);
                else
                    showTimeDialog(TIME_OUT);
                break;
        }
    }
}
