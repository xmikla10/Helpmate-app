package com.flatmate.flatmate.Other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.flatmate.flatmate.R;
import com.flatmate.flatmate.lib.ExceptionHandler;

/**
 * Created by Peter on 11/4/2016.
 */

public class TimePopUp extends Activity
{
    public static final int TIME_POPUP_FINISHED = 125;
    public static final String SELETECTED_TIME_KEY = "time_result";

    private TimePicker time_picker;
    private Button button_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set exception handler that prints info to log
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        setContentView(R.layout.popup_time);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int hight = dm.heightPixels;

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        showTime();
    }

    public void showTime()
    {
        time_picker = (TimePicker) findViewById(R.id.simpleTimePicker);
        button_time = (Button) findViewById(R.id.button_time);

        button_time.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        String selectedTime = time_picker.getCurrentHour() +":" + String.format("%02d",time_picker.getCurrentMinute());
                        Intent intent = new Intent();
                        intent.putExtra(SELETECTED_TIME_KEY,selectedTime);
                        setResult(TIME_POPUP_FINISHED,intent);
                        finish();
                    }
                }
        );
    }
}
