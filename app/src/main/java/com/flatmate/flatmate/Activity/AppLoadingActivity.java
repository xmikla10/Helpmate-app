package com.flatmate.flatmate.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.flatmate.flatmate.R;
import com.flatmate.flatmate.lib.ExceptionHandler;

/**
 * Activity that simply shows the logo and title, waits for three seconds and starts the MainPage
 */
public class AppLoadingActivity extends Activity {
    public static final int LOADING_TIME_IN_SECONDS = 4;

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
                    sleep(LOADING_TIME_IN_SECONDS * 500);  //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(AppLoadingActivity.this,
                            SignInActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}
