package com.flatmate.flatmate.Firebase;

import android.widget.Toast;

import com.flatmate.flatmate.Activity.JoinGroupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

public class FirebaseHelperFindGroup
{
    FirebaseAuth firebaseAuth;
    String user_email;
    String user_ID;
    DatabaseReference db;
    String email;
    ArrayList<NewGroup> a =new ArrayList<>();

    public FirebaseHelperFindGroup(DatabaseReference db) {
        this.db = db;
    }
    //READ
    public ArrayList<NewGroup> retrieve(String tmp)
    {
        email = tmp;
        db.child("user").child("groups").child("find").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                user_email = value.get("_user_email").toString();
                user_ID = value.get("_user_ID").toString();
                if ( user_email.equals(email))
                {
                    db.child("user").child("groups").child("user").child(user_ID).addChildEventListener(new ChildEventListener() {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                user_email = value.get("_user_email").toString();
                user_ID = value.get("_user_ID").toString();

                if ( user_email.equals(email))
                {
                    db.child("user").child("groups").child("user").addChildEventListener(new ChildEventListener() {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
        return a;
    }
    private void fetchData(DataSnapshot dataSnapshot)
    {
        a.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            NewGroup newGroup = ds.getValue(NewGroup.class);
            a.add(newGroup);
        }
    }

}