package com.flatmate.flatmate.Activity;

import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.DatePopUp;
import com.flatmate.flatmate.R;
import com.flatmate.flatmate.Other.TimePopUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Integer.parseInt;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class AddNewWorkActivity extends AppCompatActivity {

    public static final int ADD_FINISHED = 128;
    public static final String SELECTED_ADD_KEY = "date_result";
    public static final String TAG = AddNewWorkActivity.class.getSimpleName();
    String dur;
    int value;
    int a;
    String str1;
    String str2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_work);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyGroups);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final Spinner dropdown1 = (Spinner) findViewById(R.id.deadline_spinner);
        String[] items = new String[]
                {
                        getString(R.string.one_hour),
                        getString(R.string.three_hours),
                        getString(R.string.six_hours),
                        getString(R.string.twelve_hours),
                        getString(R.string.twentyfour_hours),
                        getString(R.string.fourtyeight_hours),
                        getString(R.string.week)
                };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown1.setAdapter(adapter);

        Button plus = (Button) findViewById(R.id.buttonplus);
        Button minus = (Button) findViewById(R.id.buttonminus);
        //final EditText duraction = (EditText) findViewById(R.id.editTextDuraction);

        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            String work_name = extras.getString("work_name");
            EditText ed = (EditText) findViewById(R.id.work_name);
            ed.setText(work_name);
        }

        plus.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                EditText duraction = (EditText) findViewById(R.id.duraction);
                String b = duraction.getText().toString();
                int c = Integer.parseInt(b);
                if ( c < 60)
                    c = c + 5;
                else
                    c = c + 10;
                String a = Integer.toString(c);
                duraction.setText(a);
            }
        });

        CheckBox checkbox = (CheckBox) findViewById(R.id.duration_of_auction_checkBox);
        final Switch sw2 = (Switch) findViewById(R.id.switchDate);
        final Switch sw1 = (Switch) findViewById(R.id.switchTime);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (isChecked)
                {
                    dropdown1.setVisibility(View.VISIBLE);
                }
                else
                {
                    dropdown1.setVisibility(View.INVISIBLE);
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                EditText duraction = (EditText) findViewById(R.id.duraction);
                String b = duraction.getText().toString();
                int c = Integer.parseInt(b);
                if ( c <= 60)
                    c = c - 5;
                else
                    c = c - 10;

                if ( c < 0)
                    c = 0;
                String a = Integer.toString(c);
                duraction.setText(a);
            }
        });

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (isChecked)
                {
                    startActivityForResult(new Intent(AddNewWorkActivity.this, TimePopUp.class), TimePopUp.TIME_POPUP_FINISHED);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
                else
                {
                    Switch switch1 = (Switch) findViewById(R.id.switchTime);
                    switch1.setText(R.string.time);
                }
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (isChecked)
                {
                    startActivityForResult(new Intent(AddNewWorkActivity.this, DatePopUp.class), DatePopUp.DATE_POPUP_FINISHED);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
                else
                {
                    Switch switch2 = (Switch) findViewById(R.id.switchDate);
                    switch2.setText(R.string.date);
                }
            }
        });

    }

    public void saveTask(View view)
    {
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        EditText work_name_text = (EditText) findViewById(R.id.work_name);
        EditText duration_text = (EditText) findViewById(R.id.duraction);
        Spinner deadline_spinner_text = (Spinner) findViewById(R.id.deadline_spinner);
        Switch switch1 = (Switch) findViewById(R.id.switchTime);
        Switch switch2 = (Switch) findViewById(R.id.switchDate);
        CheckBox checkbox = (CheckBox) findViewById(R.id.duration_of_auction_checkBox);


        String work_name = work_name_text.getText().toString().trim();
        String duration = duration_text.getText().toString().trim();
        String deadline_spinner = deadline_spinner_text.getSelectedItem().toString().trim();
        String date = str2;
        String time = str1;
        int maxLength = 100000000;
        Boolean control = true;

        try
        {
            Integer.parseInt(duration);
        }
        catch (Exception e) {
            control = false;
            Toast.makeText(getBaseContext(), R.string.long_duration_of_work, Toast.LENGTH_SHORT).show();
        }

        if ( control)
        {
            if ( str2 == null && str1 == null)
            {
                Toast.makeText( getBaseContext(), R.string.enter_time_date ,Toast.LENGTH_SHORT).show();
            }
            else if ( str2 == null && str1 != null)
            {
                Toast.makeText( getBaseContext(), R.string.please_date ,Toast.LENGTH_SHORT).show();
            }
            else if ( str2 != null && str1 == null)
            {
                Toast.makeText( getBaseContext(), R.string.please_time ,Toast.LENGTH_SHORT).show();
            }
            else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                Date now = new Date();
                Date myDateTime = null;

                String strDate = simpleDateFormat.format(now);
                String myString = time + " " + date;
                try {
                    myDateTime = simpleDateFormat.parse(myString);
                    now = simpleDateFormat.parse(strDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calNow = new GregorianCalendar();
                calNow.setTime(now);
                Calendar calMy = new GregorianCalendar();
                calMy.setTime(myDateTime);

                if (deadline_spinner.equals(getString(R.string.one_hour))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 2);
                } else if (deadline_spinner.equals(getString(R.string.three_hours))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 3);
                } else if (deadline_spinner.equals(getString(R.string.six_hours))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 7);
                } else if (deadline_spinner.equals(getString(R.string.twelve_hours))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 13);
                } else if (deadline_spinner.equals(getString(R.string.twentyfour_hours))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 25);
                } else if (deadline_spinner.equals(getString(R.string.fourtyeight_hours))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 49);
                } else if (deadline_spinner.equals(getString(R.string.week))) {
                    calNow.add(Calendar.HOUR_OF_DAY, 169);
                }

                if (calNow.compareTo(calMy) > 0 && checkbox.isChecked() == true && switch1.isChecked() == true && switch2.isChecked() == true) {
                    Toast.makeText(getBaseContext(), R.string.toast_add_new_work_deadline_wrong, Toast.LENGTH_LONG).show();
                } else {
                    if (work_name_text.getText().toString().trim().length() == 0) {
                        Toast.makeText(getBaseContext(), R.string.please_work_name, Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(duration) <= 0) {
                        Toast.makeText(getBaseContext(), R.string.please_duration_work, Toast.LENGTH_SHORT).show();
                    } else if (switch1.isChecked() == false) {
                        Toast.makeText(getBaseContext(), R.string.please_time, Toast.LENGTH_SHORT).show();
                    } else if (switch2.isChecked() == false) {
                        Toast.makeText(getBaseContext(), R.string.please_date, Toast.LENGTH_SHORT).show();
                    } else if (now != null && myDateTime != null) {
                        if (now.compareTo(myDateTime) > 0 && switch2.isChecked() == true && switch1.isChecked() == true)
                            Toast.makeText(getBaseContext(), R.string.wrond_time_date, Toast.LENGTH_SHORT).show();
                        else {
                            if (checkbox.isChecked() == false) {
                                deadline_spinner = "null";
                            }

                            Toast.makeText(getBaseContext(), R.string.new_work_been_added, Toast.LENGTH_SHORT).show();
                            String selectedDate = (work_name + "#"
                                    + duration + "#"
                                    + deadline_spinner + "#"
                                    + date + "#"
                                    + time + "#");
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(SELECTED_ADD_KEY, selectedDate);
                            setResult(ADD_FINISHED, intent);

                            finish();
                        }
                    } else {
                        if (checkbox.isChecked() == false) {
                            deadline_spinner = "null";
                        }

                        Toast.makeText(getBaseContext(), R.string.new_work_been_added, Toast.LENGTH_SHORT).show();
                        String selectedDate = (work_name + "#"
                                + duration + "#"
                                + deadline_spinner + "#"
                                + date + "#"
                                + time + "#");
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        intent.putExtra(SELECTED_ADD_KEY, selectedDate);
                        setResult(ADD_FINISHED, intent);

                        finish();
                    }
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data ==  null)
        {
            Switch switch1 = (Switch) findViewById(R.id.switchTime);
            Switch switch2 = (Switch) findViewById(R.id.switchDate);
            switch1.setChecked(false);
            switch2.setChecked(false);
            return;
        }
        switch (requestCode) {
            case TimePopUp.TIME_POPUP_FINISHED:

                Switch switch1 = (Switch) findViewById(R.id.switchTime);
                String[] split1 = data.getStringExtra(TimePopUp.SELETECTED_TIME_KEY).split("-");
                str1 = Arrays.toString(split1).replace("[", "").replace("]", "");
                if (str1.length() == 4)
                {
                    str1 = "0" + str1;
                }
                switch1.setText(getString(R.string.time_and) +  str1);
                Toast.makeText(this, str1 , Toast.LENGTH_SHORT).show();
                break;

            case DatePopUp.DATE_POPUP_FINISHED:

                Switch switch2 = (Switch) findViewById(R.id.switchDate);
                String[] split2 = data.getStringExtra(DatePopUp.SELECTED_DATE_KEY).split("-");
                str2 = Arrays.toString(split2).replace("[", "").replace("]", "");
                switch2.setText(getString(R.string.date_and) + str2);
                Toast.makeText(this, str2 , Toast.LENGTH_SHORT).show();
                break;

            case AppPreferences.SETTINGS_FINISHED:
                if ( data == null)
                {
                    return;
                }
                String control = data.getStringExtra("control");

                if ( control.equals("1"))
                {
                    String renameUser = data.getStringExtra("rename");
                    Intent intent = new Intent(AddNewWorkActivity.this, AddNewWorkActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }

                if ( control.equals("2"))
                {
                    Intent intent = new Intent(AddNewWorkActivity.this, AddNewWorkActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }

                break;

            default:
                Log.d(TAG, "onActivityResult: uknown request code " + requestCode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (id == R.id.nav_settings)
        {
            startActivityForResult(new Intent(AddNewWorkActivity.this, AppPreferences.class), AppPreferences.SETTINGS_FINISHED);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.to_do, menu);
        return true;
    }

}