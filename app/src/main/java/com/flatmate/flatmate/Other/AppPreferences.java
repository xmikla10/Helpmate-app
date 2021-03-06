package com.flatmate.flatmate.Other;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.CompoundButton;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.flatmate.flatmate.Activity.MainActivity;
import com.flatmate.flatmate.Activity.MyGroupsActivity;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.GraphUser;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppPreferences extends PreferenceActivity
{
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final int SETTINGS_FINISHED = 111;


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
        public DatabaseReference db;
        public FirebaseHelperWork helper;
        private FirebaseAuth firebaseAuth;
        public String userID;
        public String userName;
        public String newName;
        public String user;
       // public String childKey;
        EditTextPreference edPref;


        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);

            edPref = (EditTextPreference) findPreference("username");

            firebaseAuth = FirebaseAuth.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid().toString();

            db = FirebaseDatabase.getInstance().getReference();
            db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener()
            {
                @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    userName = value.get("_name").toString();
                    edPref.setSummary(userName);
                    edPref.setText(userName);
                }
                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override public void onCancelled(DatabaseError databaseError) {}
            });

            ListPreference listPreference = (ListPreference) findPreference("language");
            if(listPreference.getValue()==null)
            {
                SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredText = prefs.getString("language", null);
                if( restoredText != null)
                {
                    if ( restoredText.equals("sk"))
                    {
                        listPreference.setValueIndex(2);
                    }
                    if ( restoredText.equals("cs"))
                    {
                        listPreference.setValueIndex(1);
                    }
                    if ( restoredText.equals("en"))
                    {
                        listPreference.setValueIndex(0);
                    }
                }
                else
                    listPreference.setValueIndex(0);
            }
            else
            {
                SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredText = prefs.getString("language", null);
                if( restoredText != null)
                {
                    if ( restoredText.equals("sk"))
                    {
                        listPreference.setValueIndex(2);
                    }
                    if ( restoredText.equals("cs"))
                    {
                        listPreference.setValueIndex(1);
                    }
                    if ( restoredText.equals("en"))
                    {
                        listPreference.setValueIndex(0);
                    }
                }
                else
                    listPreference.setValueIndex(0);
            }
            listPreference.setSummary(listPreference.getValue().toString());
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    preference.setSummary(newValue.toString());
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
                    Toast.makeText(getActivity(), newValue.toString() , Toast.LENGTH_SHORT).show();
                    return true;
                }
            });


            edPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, final Object newValue)
                {
                    preference.setSummary(newValue.toString());
                    newName = newValue.toString();

                    if ( !userName.equals(newValue.toString()))
                    {
                        db.child("user").child("users").child(userID).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                {
                                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                    String childKey = childSnapshot.getKey();
                                    String oldUserName = value.get("_name").toString();

                                    Map newNameMap = new HashMap();
                                    newNameMap.put("_name", newName);
                                    db.child("user").child("users").child(userID).child("data").child(childKey).updateChildren(newNameMap);
                                }

                                db.child("user").child("groups").child("user").child(userID).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                        {
                                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                            final String groupID = value.get("_group_ID").toString();

                                            db.child("user").child("groups").child("members").child(groupID).child("members").orderByChild("_user_ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override public void onDataChange(DataSnapshot dataSnapshot)
                                                {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                                    {
                                                        String childKey = childSnapshot.getKey();

                                                        Map newNameMap = new HashMap();
                                                        newNameMap.put("_user_name", newName);
                                                        db.child("user").child("groups").child("members").child(groupID).child("members").child(childKey).updateChildren(newNameMap);
                                                    }
                                                }
                                                @Override public void onCancelled(DatabaseError databaseError) {}});
                                        }
                                    }
                                    @Override public void onCancelled(DatabaseError databaseError) {}});

                            }
                            @Override public void onCancelled(DatabaseError databaseError) {}});
                    }

                    //Intent refresh = new Intent(getContext(), MainActivity.class);
                    //startActivity(refresh);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("rename", newName);
                    intent.putExtra("control", "1");
                    getActivity().setResult(SETTINGS_FINISHED, intent);
                    getActivity().finish();
                    Toast.makeText(getActivity(), newName , Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        public String getMonth(Integer month)
        {
            String monthString;
            switch (month)
            {
                case 1:  monthString = "January";break;
                case 2:  monthString = "February";break;
                case 3:  monthString = "March";break;
                case 4:  monthString = "April";break;
                case 5:  monthString = "May";break;
                case 6:  monthString = "June";break;
                case 7:  monthString = "July";break;
                case 8:  monthString = "August";break;
                case 9:  monthString = "September";break;
                case 10: monthString = "October";break;
                case 11: monthString = "November";break;
                case 12: monthString = "December";break;
                default: monthString = "Invalid month";break;
            }

            return monthString;
        }

        public void setLocale(String lang)
        {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("language", lang);
            editor.commit();

            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;

            res.updateConfiguration(conf, dm);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("control", "2");
            getActivity().setResult(SETTINGS_FINISHED, intent);
            getActivity().finish();
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
