package com.flatmate.flatmate.Firebase;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flatmate.flatmate.Other.SetNotification;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class FirebaseHelperWork
{
    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    NewWork newWork1;
    String userID;
    String uniqueID;
    Boolean saved=null;
    ArrayList<NewWork> a =new ArrayList<>();

    public FirebaseHelperWork(DatabaseReference db) {
        this.db = db;
    }
    //WRITE
    public Boolean save(final NewWork newWork)
    {
        if(newWork == null)
        {
            saved=false;
        }else
        {
            try
            {
                newWork1 = newWork;
                firebaseAuth = FirebaseAuth.getInstance();
                uniqueID = UUID.randomUUID().toString();
                userID = firebaseAuth.getCurrentUser().getUid().toString();
                db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
                     @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                         Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                         groupID = value.get("_group").toString();
                         if(groupID.length() != 0)
                         {
                             db.child("groups").child(groupID).child("works").child("todo").push().setValue(newWork1);
                             SetNotification set = new SetNotification();
                             set.Set(groupID, 1, newWork.get_work_name(), newWork.get_bidsID(), "");
                         }
                     }
                     @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                     @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                     @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                     @Override public void onCancelled(DatabaseError databaseError) {}
                 });

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
    public ArrayList<NewWork> retrieve()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
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
                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
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