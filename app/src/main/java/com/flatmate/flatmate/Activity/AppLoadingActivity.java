package com.flatmate.flatmate.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.flatmate.flatmate.R;
import com.flatmate.flatmate.lib.ExceptionHandler;

import java.util.Locale;

/**
 * Activity that simply shows the logo and title, waits for three seconds and starts the MainPage
 */
public class AppLoadingActivity extends Activity {
    public static final int LOADING_TIME_IN_SECONDS = 4;
    public static final String MY_PREFS_NAME = "MyPrefsFile";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_loading);

        // set exception handler that prints info to log
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(LOADING_TIME_IN_SECONDS * 500);
                } catch (Exception e) {

                } finally {

                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        String restoredText = prefs.getString("language", null);

                        if (restoredText != null)
                        {
                            Locale myLocale = new Locale(restoredText);
                            Resources res = getResources();
                            DisplayMetrics dm = res.getDisplayMetrics();
                            Configuration conf = res.getConfiguration();
                            conf.locale = myLocale;
                            res.updateConfiguration(conf, dm);
                        }

                    Intent i = new Intent(AppLoadingActivity.this,
                            SignInActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}
