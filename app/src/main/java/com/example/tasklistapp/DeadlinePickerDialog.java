package com.example.tasklistapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DeadlinePickerDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DeadlineListener {
        void onDeadlineSelected(Date deadline);
    }
    DeadlineListener mDeadlineListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mDeadlineListener = (DeadlineListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(getActivity().toString() + " must implement DeadlineListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date deadline = new GregorianCalendar(year, month, dayOfMonth).getTime();
        mDeadlineListener.onDeadlineSelected(deadline);
    }

}
