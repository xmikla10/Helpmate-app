package com.flatmate.flatmate.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.flatmate.flatmate.Other.SetNotification;
import com.flatmate.flatmate.R;

import java.util.Locale;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class SelectLanguageActivity extends AppCompatActivity
{
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language_layout);
    }

    public void selectEnglishLanguage(View view)
    {
        setLanguage("en");
    }

    public void selectSlovakLanguage(View view)
    {
        setLanguage("sk");

    }

    public void selectCzechLanguage(View view)
    {
        setLanguage("cs");

    }

    public void setLanguage(String lang)
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.putString("firstStart", "true");
        editor.commit();

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent i = new Intent(SelectLanguageActivity.this, SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
        finish();

    }
}
