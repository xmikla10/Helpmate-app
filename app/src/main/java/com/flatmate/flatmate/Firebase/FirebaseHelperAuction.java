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

public class FirebaseHelperAuction
{
    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    NewBid newbid1;
    String userID;
    String uniqueID;
    Boolean saved=null;
    ArrayList<NewBid> a =new ArrayList<>();

    public FirebaseHelperAuction(DatabaseReference db) {
        this.db = db;
    }
    //WRITE
    public Boolean save(NewBid newbid, final String bidsID, final String group)
    {
        if(newbid == null)
        {
            saved=false;
        }else
        {
            try
            {
                groupID = group;
                newbid1 = newbid;
                firebaseAuth = FirebaseAuth.getInstance();
                uniqueID = UUID.randomUUID().toString();
                userID = firebaseAuth.getCurrentUser().getUid().toString();

                if (groupID.length() != 0)
                {
                    db.child("groups").child(groupID).child("bids").child(bidsID).child("bids").push().setValue(newbid1);
                }

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

    public ArrayList<NewBid> retrieve(final String bidsID)
    {
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("bids").child(bidsID).addChildEventListener(new ChildEventListener() {
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
                db.child("groups").child(groupID).child("bids").child(bidsID).addChildEventListener(new ChildEventListener() {
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
            NewBid newbid = ds.getValue(NewBid.class);
            a.add(newbid);
        }
    }

}