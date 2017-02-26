package com.flatmate.flatmate.Other;

/**
 * Created by xmikla10 on 16.11.2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.FirebaseHelperWorkCompleted;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab_COMPLETED extends Fragment {

    private ListView listView1;
    private FirebaseAuth firebaseAuth;
    private String groupID;
    String userID;
    DatabaseReference db;
    FirebaseHelperWorkCompleted helper;
    CustomAdapterCompleted adapter;
    NewWork newWork;
    ListView lv;
    ArrayList<NewWork> a =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        final View rootView = inflater.inflate(R.layout.activity_completed_layout, container, false);
        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperWorkCompleted(db);

        adapter = new CustomAdapterCompleted(getContext(), helper.retrieve());
        lv = (ListView) rootView.findViewById(R.id.listViewCompleted);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();

                db.child("groups").child(groupID).child("completed").child("works").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println(snapshot.getValue());
                        if (snapshot.getValue() == null)
                        {
                            ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                            mProgress.setVisibility(View.GONE);

                        }
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {} });

                db.child("groups").child(groupID).child("completed").addChildEventListener(new ChildEventListener() {
                    ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        adapter = new CustomAdapterCompleted(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        adapter = new CustomAdapterCompleted(getContext(), helper.retrieve());
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
                db.child("groups").child(groupID).child("completed").addChildEventListener(new ChildEventListener()
                {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        adapter = new CustomAdapterCompleted(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        adapter = new CustomAdapterCompleted(getContext(), helper.retrieve());
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
        return rootView;
        // stačí kontrolovať v databáze status u jednotlivých prác a zobraziť ich tu ak majú status finished ...
        // ak to nepôjde, po dokončení práce sa z to do vymaže práca a uloží sa do inej časti databáze v ktorej budem zobrazovať všetko čo je vnej
        // a to by možno aj bolo prehladnejšie, pretože práce budú pribúdať
    }
}