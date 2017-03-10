package com.flatmate.flatmate.Other;

import android.app.Activity;
import android.preference.CheckBoxPreference;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.CompoundButton;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import com.flatmate.flatmate.R;

import java.util.Locale;

public class AppPreferences extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
                    Toast.makeText(getActivity(), "Set language : "+newValue.toString() ,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

}
