package com.michaeldpalmer.timetracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

public class RecordFragment extends Fragment implements Button.OnClickListener {

    TimePicker t = null;
    Button b = null;
    Switch type = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);
        t = (TimePicker) rootView.findViewById(R.id.timePicker);
        b = (Button) rootView.findViewById(R.id.btnRecord);
        type = (Switch) rootView.findViewById(R.id.clockType);
        b.setOnClickListener(this);
        return rootView;
    }

    public void recordTime() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        DateTime date = new DateTime().withTime( t.getCurrentHour(), t.getCurrentMinute(), 0, 0);

        db.addClocking(
                new Clocking(
                        type.isChecked() ? Clocking.CLOCK_IN : Clocking.CLOCK_OUT,
                        MainActivity.PAY_PERIOD,
                        Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(date),
                        true
                )
        );

        type.toggle();
        MainActivity.viewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        recordTime();
    }
}