package com.telran.officecrimereporter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends AppCompatDialogFragment {
    public static final String EXTRA_CR_TIME = "updated time" ;
    private TimePicker timePicker;
  public static final String ARG_TIME = "time of crime";

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_time_picker,null);
        timePicker = v.findViewById(R.id.timePicker);
        java.util.Date date = (java.util.Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hr = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(hr);
            timePicker.setMinute(min);
        }
        return new AlertDialog.Builder(requireContext())
                .setView(timePicker)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hrN;
                        int minN;
                        if (Build.VERSION.SDK_INT >= 23) {
                            hrN = timePicker.getHour();
                            minN = timePicker.getMinute();
                        }else{
                             hrN = timePicker.getCurrentHour();
                             minN = timePicker.getCurrentMinute();
                    }
                        Date nDate = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),hrN,minN).getTime();
                        sendResult(Activity.RESULT_OK,nDate);
                    }
                })
                .create();

    }

    public static TimePickerFragment newInstance(Date date){
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TIME,date);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void sendResult(int resultCode, java.util.Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CR_TIME, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),resultCode,intent);

    }
}
