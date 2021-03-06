package com.flatmate.flatmate.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.FirebaseHelperAuction;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyGroups;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyWorks;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.CustomAdapterMyGroups;
import com.flatmate.flatmate.Other.CustomAdapterMyWorks;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class MyGroupsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String groupID;
    String childKey;
    String userID;
    public static final int GROUP_FINISHED = 100;
    String userEmail;
    DatabaseReference db;
    FirebaseHelperMyGroups helper;
    CustomAdapterMyGroups adapter;
    ListView lv;
    LinearLayout lin;
    int positionHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();

        setContentView(R.layout.activity_my_groups_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyGroups);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperMyGroups(db);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();

        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this , helper.retrieve(), groupID);
        lv = (ListView) findViewById(R.id.listViewMyGroups);

        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("user").child("groups").child("user").child(userID).child("user").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot)
                    {
                        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("user").child("groups").child("user").child(userID).child("user").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot)
                    {
                        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                adapter = new CustomAdapterMyGroups(MyGroupsActivity.this, helper.retrieve(),groupID);
                lv.setAdapter(adapter);
            }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                positionHelp = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MyGroupsActivity.this);
                builder.setMessage(R.string.activate_group)
                        .setNegativeButton(R.string.activate, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                final NewGroup s= (NewGroup) adapter.getItem(positionHelp);
                                userID = firebaseAuth.getCurrentUser().getUid().toString();

                                db.child("user").child("users").child(userID).child("data").orderByChild("_ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                        {
                                            childKey = childSnapshot.getKey();
                                        }
                                        Map newUserData = new HashMap();
                                        newUserData.put("_group", s.get_group_ID());

                                        db.child("user").child("users").child(userID).child("data").child(childKey).updateChildren(newUserData);

                                        Intent intent = new Intent(MyGroupsActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("my_groups_activity_result", "1");
                                        setResult(GROUP_FINISHED, intent);
                                        finish();
                                    }
                                    @Override public void onCancelled(DatabaseError databaseError) {}
                            });
                            }
                        })
                        .setPositiveButton(R.string.info, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                final NewGroup s= (NewGroup) adapter.getItem(positionHelp);
                                userID = firebaseAuth.getCurrentUser().getUid().toString();

                                db.child("user").child("users").child(userID).child("data").orderByChild("_ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                        {
                                            childKey = childSnapshot.getKey();
                                        }

                                        db.child("user").child("groups").child("user").child(userID).child("user").orderByChild("_group_ID").equalTo(s.get_group_ID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                String admin = "";
                                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                                {
                                                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                    admin = value.get("_admin").toString();
                                                }

                                                Intent intent = new Intent(MyGroupsActivity.this, GroupInfoActivity.class);
                                                intent.putExtra("group_ID", s.get_group_ID());
                                                intent.putExtra("group_name", s.get_group_name());
                                                intent.putExtra("admin", admin);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            }
                                            @Override public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                    @Override public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case AppPreferences.SETTINGS_FINISHED:
                if (data == null) {
                    return;
                }
                String control = data.getStringExtra("control");

                if (control.equals("1")) {
                    String renameUser = data.getStringExtra("rename");
                    Intent intent = new Intent(MyGroupsActivity.this, MyGroupsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }

                if (control.equals("2")) {
                    Intent intent = new Intent(MyGroupsActivity.this, MyGroupsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }

                break;


            default:
                Log.d("test", "onActivityResult: uknown request code " + requestCode);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private void setLocale()
    {
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
        else
        {
            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

    }
}