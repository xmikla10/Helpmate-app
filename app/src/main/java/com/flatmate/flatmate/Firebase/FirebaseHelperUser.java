package com.flatmate.flatmate.Firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class FirebaseHelperUser
{
    DatabaseReference db;
    Boolean saved=null;
    ArrayList<NewUser> a =new ArrayList<>();

    public FirebaseHelperUser(DatabaseReference db) {
        this.db = db;
    }
    //WRITE
    public Boolean save(NewUser newUser)
    {
        if(newUser == null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("user").child("users").child(newUser.get_ID()).push().setValue(newUser);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    //READ
    public ArrayList<NewUser> retrieve()
    {
        db.child("user").child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return a;
    }
    private void fetchData(DataSnapshot dataSnapshot)
    {
        a.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            NewUser newUser = ds.getValue(NewUser.class);
            a.add(newUser);

        }
    }

}