package com.michaeldpalmer.timetracker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

public class HolidayDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_holiday, container, false);

        int month = getArguments().getInt("month") + 1;
        int day = getArguments().getInt("day");
        int year = getArguments().getInt("year");

        LocalDate date = LocalDate.parse(String.format("%d-%02d-%02d", year, month, day));

        super.getDialog().setTitle(date.toString("EEE, MMM d, yyyy"));

        return rootView;
    }
}
