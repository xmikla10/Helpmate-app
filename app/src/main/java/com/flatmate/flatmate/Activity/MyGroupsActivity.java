package com.flatmate.flatmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.FirebaseHelperAuction;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyGroups;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyWorks;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
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
    String userEmail;
    DatabaseReference db;
    FirebaseHelperMyGroups helper;
    CustomAdapterMyGroups adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups_layout);

        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperMyGroups(db);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();

        adapter = new CustomAdapterMyGroups(MyGroupsActivity.this , helper.retrieve(), groupID);
        lv = (ListView) findViewById(R.id.listViewMyGroups);

        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
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
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NewGroup s= (NewGroup) adapter.getItem(position);
                userID = firebaseAuth.getCurrentUser().getUid().toString();

                db.child("user").child("users").child(userID).orderByChild("_ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            childKey = childSnapshot.getKey();
                        }
                        Map newUserData = new HashMap();
                        newUserData.put("_group", s.get_group_ID());

                        db.child("user").child("users").child(userID).child(childKey).updateChildren(newUserData);
                        finish();
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });

    }

}