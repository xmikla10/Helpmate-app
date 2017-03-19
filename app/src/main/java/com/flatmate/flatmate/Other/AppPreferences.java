package com.flatmate.flatmate.Other;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.CompoundButton;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import com.flatmate.flatmate.Activity.MainActivity;
import com.flatmate.flatmate.R;

import java.util.Locale;

public class AppPreferences extends PreferenceActivity
{
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }


    public static class PrefsFragment extends PreferenceFragment
    {
        Locale myLocale;
        CheckBoxPreference checkbox;
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);

            checkbox = (CheckBoxPreference) findPreference("notification");
            if ( checkbox.isChecked() == false)
                checkbox.setSummary("Notification Off");

            checkbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                public boolean onPreferenceClick(Preference preference)
                {
                    if ( checkbox.isChecked())
                    {
                        checkbox.setSummary("Notification On");
                        Toast.makeText(getActivity(), "Notification On", Toast.LENGTH_SHORT).show();
                    }
                    else if ( checkbox.isChecked() == false) {
                        checkbox.setSummary("Notification Off");
                        Toast.makeText(getActivity(),"Notification Off", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            ListPreference listPreference = (ListPreference) findPreference("language");
            if(listPreference.getValue()==null)
            {
                listPreference.setValueIndex(0);
            }
            listPreference.setSummary(listPreference.getValue().toString());
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    preference.setSummary(newValue.toString());
                    System.out.println("----------> " + newValue.toString());
                    String language = newValue.toString();

                    if ( language.equals("Slovenský"))
                    {
                        setLocale("sk");
                    }
                    if ( language.equals("Český"))
                    {
                        setLocale("cs");
                    }
                    if ( language.equals("English"))
                    {
                        setLocale("en");
                    }
                    Toast.makeText(getActivity(), "Set language : "+newValue.toString() , Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        public void setLocale(String lang)
        {
            SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("language", lang);
            editor.commit();

            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(getContext(), MainActivity.class);
            startActivity(refresh);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(android.R.id.home ==  item.getItemId() )
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
