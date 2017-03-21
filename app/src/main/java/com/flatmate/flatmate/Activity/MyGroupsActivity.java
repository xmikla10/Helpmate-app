package com.flatmate.flatmate.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.CustomAdapterMyGroups;
import com.flatmate.flatmate.Other.CustomAdapterMyWorks;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

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
        lin = (LinearLayout) findViewById(R.id.groupInfoLinLayout);

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
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
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
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
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

        if (id == R.id.nav_settings)
        {
            Intent intent6 = new Intent(this, AppPreferences.class);
            intent6.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent6);
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
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