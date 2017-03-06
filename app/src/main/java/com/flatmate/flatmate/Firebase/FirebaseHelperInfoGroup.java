package com.flatmate.flatmate.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseHelperInfoGroup
{
    FirebaseAuth firebaseAuth;
    String user_email;
    String user_ID;
    DatabaseReference db;
    String email;
    ArrayList<NewGroup> a =new ArrayList<>();

    public FirebaseHelperInfoGroup(DatabaseReference db) {
        this.db = db;
    }
    //READ
    public ArrayList<NewGroup> retrieve(String group_ID)
    {

        db.child("user").child("groups").child("members").child(group_ID).addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) { fetchData(dataSnapshot);}
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { fetchData(dataSnapshot);}
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