package com.flatmate.flatmate.Other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.flatmate.flatmate.R;
import com.flatmate.flatmate.lib.ExceptionHandler;

public class DatePopUp extends Activity
{
    public static final int DATE_POPUP_FINISHED = 126;
    public static final String SELECTED_DATE_KEY = "date_result";

    private DatePicker date_picker;
    private Button button_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set exception handler that prints info to log
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        setContentView(R.layout.popup_date);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int hight = dm.heightPixels;

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        showTime();

    }

    public void showTime()
    {
        date_picker = (DatePicker) findViewById(R.id.datePicker);
        button_date = (Button) findViewById(R.id.button_date);

        button_date.setOnClickListener( new View.OnClickListener()
                                        {
                                            public void onClick(View v)
                                            {
                                                Integer month = date_picker.getMonth()+1;
                                                Integer day = date_picker.getDayOfMonth();
                                                String monthFinal;
                                                String dayFinal;

                                                if ( month < 10)
                                                {
                                                    monthFinal = "0" + month.toString();
                                                }
                                                else
                                                    monthFinal = month.toString();


                                                if ( day < 10)
                                                {
                                                    dayFinal = "0" + day.toString();
                                                }
                                                else
                                                    dayFinal = day.toString();


                                                String selectedDate = dayFinal +"/"
                                                        + monthFinal+"/"
                                                        + (date_picker.getYear());
                                                Intent intent = new Intent();
                                                intent.putExtra(SELECTED_DATE_KEY,selectedDate);
                                                setResult(DATE_POPUP_FINISHED,intent);
                                                finish();

                                            }
                                        }
        );

    }
}
