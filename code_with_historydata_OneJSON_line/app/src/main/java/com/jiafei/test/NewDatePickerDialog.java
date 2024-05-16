package com.jiafei.test;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class NewDatePickerDialog extends DatePickerDialog {
    private String[] mDisplayMonths = {"1", "2", "3","4", "5", "6","7", "8", "9","10", "11", "12"};
    @RequiresApi(api = Build.VERSION_CODES.N)
    public NewDatePickerDialog(@NonNull Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NewDatePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public NewDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public NewDatePickerDialog(@NonNull Context context, int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mSpinners = (LinearLayout) findViewById(getContext().getResources().getIdentifier("android:id/pickers", null, null));
        if (mSpinners != null) {
            NumberPicker mYearSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
            NumberPicker mMonthSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
            NumberPicker mDaySpinner = (NumberPicker)findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
            mSpinners.removeAllViews();
            if (mYearSpinner != null) {
                mSpinners.addView(mYearSpinner);
            }
            if (mMonthSpinner != null) {
                mSpinners.addView(mMonthSpinner);
            }
            if (mDaySpinner != null) {
                mSpinners.addView(mDaySpinner);
            }
        }
    }

    @Override
    public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
        super.onDateChanged(view, year, month, dayOfMonth);
        setTitle(year+"年 "+(month+1)+"月 "+dayOfMonth+"日");
        //关键行
        ((NumberPicker)((ViewGroup)((ViewGroup)view.getChildAt(0)).getChildAt(0)).getChildAt(1)).setDisplayedValues(mDisplayMonths);
    }
}


