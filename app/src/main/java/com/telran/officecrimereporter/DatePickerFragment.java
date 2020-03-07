package com.telran.officecrimereporter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends AppCompatDialogFragment {
    private DatePicker datePicker;
    public static final String ARG_DATE = "date";
    public static final String EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
         super.onCreateDialog(savedInstanceState);
         Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date,null);
        datePicker = v.findViewById(R.id.dialog_date_picker);

        datePicker.init(year,month,day,null);
         return new AlertDialog.Builder(requireContext())
                 .setView(v)
                 .setTitle(R.string.date_picker_title)
                 .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                     int y = datePicker.getYear();
                     int m = datePicker.getMonth();
                     int d = datePicker.getDayOfMonth();
                     Date date1 = new GregorianCalendar(y,m,d).getTime();
                     sendResult(Activity.RESULT_OK,date1);
                 })
                 .create();
    }

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date){
        if (getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
