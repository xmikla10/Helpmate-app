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

public class FirebaseHelperMyWorks
{
    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    NewWork newWork1;
    String userID;
    String uniqueID;
    Boolean saved=null;
    ArrayList<NewWork> a =new ArrayList<>();

    public FirebaseHelperMyWorks(DatabaseReference db) {
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
                db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
                     @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                         Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                         groupID = value.get("_group").toString();
                         if(groupID.length() != 0)
                         {
                             db.child("groups").child(groupID).child("works").child("todo").push().setValue(newWork1);
                             //String bidsID = newWork1.get_bidsID();
                             //db.child("groups").child(groupID).child("bids").child("todo").child(bidsID).push().setValue("");
                         }
                     }
                     @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                         Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                         groupID = value.get("_group").toString();
                         db.child("groups").child(groupID).child("works").child("todo").push().setValue(newWork1);
                         //String bidsID = newWork1.get_bidsID();
                         //db.child("groups").child(groupID).child("bids").child("todo").child(bidsID).push().setValue("");
                     }
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
        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
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
        firebaseAuth = FirebaseAuth.getInstance();
        String userEmail = firebaseAuth.getCurrentUser().getEmail().toString();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            NewWork newWork = ds.getValue(NewWork.class);
            String email = newWork.get_userEmail();
            if ( userEmail.equals(email))
            {
                a.add(newWork);
            }
        }
    }

}