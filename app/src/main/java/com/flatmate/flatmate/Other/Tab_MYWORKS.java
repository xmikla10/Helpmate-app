package com.flatmate.flatmate.Other;

/**
 * Created by xmikla10 on 16.11.2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.flatmate.flatmate.Activity.AuctionActivity;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyWorks;
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
public class Tab_MYWORKS extends Fragment {

    private ListView listView1;
    private FirebaseAuth firebaseAuth;
    private String groupID;
    String userID;
    String userEmail;
    DatabaseReference db;
    FirebaseHelperMyWorks helper;
    CustomAdapterMyWorks adapter;
    NewWork newWork;
    ListView lv;
    ArrayList<NewWork> a =new ArrayList<>();

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.activity_my_works_layout, container, false);
        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperMyWorks(db);

        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();

        adapter = new CustomAdapterMyWorks(getContext(), helper.retrieve());
        lv = (ListView) rootView.findViewById(R.id.listViewMyWorks);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                if(groupID.length() == 0)
                {
                    ProgressBar mProgress= (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                    mProgress.setVisibility(View.GONE);
                }

                db.child("groups").child(groupID).child("works").addValueEventListener(new ValueEventListener() {
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

                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
                    ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterMyWorks(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterMyWorks(getContext(), helper.retrieve());
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
                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener()
                {
                    ProgressBar mProgress= (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterMyWorks(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterMyWorks(getContext(), helper.retrieve());
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final NewWork s= (NewWork) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), AuctionActivity.class);
                intent.putExtra("work_name", s.get_work_name());
                intent.putExtra("status", s.get_status());
                intent.putExtra("duration", s.get_duration());
                intent.putExtra("deadline", s.get_deadline());
                intent.putExtra("time", ( s.get_time() +"  "+s.get_date()));
                intent.putExtra("bidsID", s.get_bidsID());
                intent.putExtra("myWork", "1");
                intent.putExtra("workProgress", s.get_workProgress());


                startActivity(intent);
            }

        });
        return rootView;
        // my works budú zobrazovať to isté čo v to do ale stým rozdielom, že pridám do databáze k samotným prácam meno, ktorý to bude robiť a
        // keď to meno bude zhodné s prihláseným, tak sa zobrazí tuto ... nepoužívať meno ale email pre identifikáciu ... alebo ID
    }
}