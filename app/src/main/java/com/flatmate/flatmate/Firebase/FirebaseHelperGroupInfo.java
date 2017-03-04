package com.flatmate.flatmate.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class FirebaseHelperGroupInfo
{
    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    NewWork newWork1;
    String userID;
    String uniqueID;
    Boolean saved=null;
    ArrayList<NewWork> a =new ArrayList<>();

    public FirebaseHelperGroupInfo(DatabaseReference db) {
        this.db = db;
    }
    //WRITE

    //READ
    public ArrayList<NewWork> retrieve()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("completed").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("completed").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
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
            NewWork newWork = ds.getValue(NewWork.class);
            a.add(newWork);
        }
    }

}