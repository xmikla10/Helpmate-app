package com.flatmate.flatmate.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class CreateNewGroupActivity extends AppCompatActivity {

    String group_name;
    String user_name;
    String user_email;
    String group_ID;
    String userID;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_layout);

        FloatingActionButton createGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final NewGroup newGroupUser = new NewGroup();
                final NewGroup newGroupMembers = new NewGroup();
                final NewGroup newGroupFind = new NewGroup();

                EditText groupName = (EditText) findViewById(R.id.editTextGroupName);
                group_name = groupName.getText().toString();
                group_ID = UUID.randomUUID().toString();

                db = FirebaseDatabase.getInstance().getReference();
                firebaseAuth = FirebaseAuth.getInstance();
                userID = firebaseAuth.getCurrentUser().getUid().toString();

                db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        user_email= value.get("_email").toString();
                        user_name = value.get("_name").toString();

                        newGroupUser.set_group_ID(group_ID);
                        newGroupUser.set_user_email(user_email);
                        newGroupUser.set_group_name(group_name);
                        newGroupUser.set_admin("true");
                        db.child("user").child("groups").child("user").child(userID).child("user").push().setValue(newGroupUser);

                        newGroupMembers.set_user_email(user_email);
                        newGroupMembers.set_user_ID(userID);
                        newGroupMembers.set_user_name(user_name);
                        db.child("user").child("groups").child("members").child(group_ID).push().setValue(newGroupMembers);

                        newGroupFind.set_user_email(user_email);
                        newGroupFind.set_user_ID(userID);
                        db.child("user").child("groups").child("find").push().setValue(newGroupFind);
                        finish();
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

            }
        });

    }

}