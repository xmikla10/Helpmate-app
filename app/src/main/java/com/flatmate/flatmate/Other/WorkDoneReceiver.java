package com.flatmate.flatmate.Other;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WorkDoneReceiver extends WakefulBroadcastReceiver
{

    private String groupID;
    String childKey;
    DatabaseReference db;
    String  bidsID;
    private FirebaseAuth firebaseAuth;
    String userID;

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        int alarmId = intent.getExtras().getInt("alarmId");
        bidsID = intent.getExtras().getString("bidsID");
        groupID = intent.getExtras().getString("groupID");
        childKey= intent.getExtras().getString("childKey");

        db = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        db.child("groups").child(groupID).child("works").child("todo").child(childKey).removeValue();
        db.child("groups").child(groupID).child("bids").child(bidsID).setValue(null);
        db.child("groups").child(groupID).child("bids").child(bidsID).removeValue();

    }
}
