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
                        String firstStart = prefs.getString("firstStart", null);

                        if ( firstStart == null)
                        {
                            String language = Locale.getDefault().getLanguage();

                            if( language.equals("sk"))
                            {
                                setLocale("sk");
                            }
                            else if( language.equals("cs"))
                            {
                                setLocale("cs");
                            }
                            else
                            {
                                setLocale("en");
                            }


                            Intent i = new Intent(AppLoadingActivity.this, IntroActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("firstStart", "1");
                            editor.commit();

                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            if (restoredText != null)
                            {
                                Locale myLocale = new Locale(restoredText);
                                Resources res = getResources();
                                DisplayMetrics dm = res.getDisplayMetrics();
                                Configuration conf = res.getConfiguration();
                                conf.locale = myLocale;
                                res.updateConfiguration(conf, dm);
                            }

                            Intent i = new Intent(AppLoadingActivity.this, SignInActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(i);
                            finish();

                        }
                }
            }
        };
        welcomeThread.start();
    }

    public void setLocale(String lang)
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.commit();

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;

        res.updateConfiguration(conf, dm);
    }

}
