package com.flatmate.flatmate.Other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TimePicker;

import com.flatmate.flatmate.R;
import com.flatmate.flatmate.lib.ExceptionHandler;

/**
 * Created by Peter on 11/4/2016.
 */

public class BidPopUp extends Activity
{

    public static final String SELETECTED_BID_KEY = "bid_result";
    public static final int BID_POPUP_FINISHED = 125;
    NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set exception handler that prints info to log
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        setContentView(R.layout.popup_bid);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btnbid = (Button) findViewById(R.id.button_bid);
        final CheckBox checkboxbid = (CheckBox) findViewById(R.id.checkBoxBid);

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(false);

        btnbid.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                int np_count = np.getValue();
                String bid = np_count + " credits";
                if ( checkboxbid.isChecked() )
                {
                    bid = "not interested";
                }
                Intent intent = new Intent();
                intent.putExtra(SELETECTED_BID_KEY, bid);
                setResult(BID_POPUP_FINISHED,intent);
                finish();
            }
        });






    }
}
