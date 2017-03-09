package com.flatmate.flatmate.Other;

/**
 * Created by xmikla10 on 16.11.2016.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flatmate.flatmate.Activity.AddNewWorkActivity;
import com.flatmate.flatmate.Activity.AuctionActivity;
import com.flatmate.flatmate.Activity.MainActivity;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab_TODO extends Fragment
{
    private ListView listView1;
    private FirebaseAuth firebaseAuth;
    private String groupID;
    String userID;
    DatabaseReference db;
    FirebaseHelperWork helper;
    CustomAdapterToDo adapter;
    NewWork newWork;
    ListView lv;
    ArrayList<NewWork> a =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.activity_to_do, container, false);

        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperWork(db);

        adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
        lv = (ListView) rootView.findViewById(R.id.listView1);

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
                        if (snapshot.getValue() == null)
                        {
                            ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                            mProgress.setVisibility(View.GONE);
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.VISIBLE);
                            adapter = null;
                            lv.setAdapter(adapter);
                        }
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {} });

                    db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
                        ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                            adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                            lv.setAdapter(adapter);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s)
                        {
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                            adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot)
                        {
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                            adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                            lv.setAdapter(adapter);
                        }
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
                        LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                        lay.setVisibility(View.GONE);
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                        lay.setVisibility(View.GONE);
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NewWork s= (NewWork) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), AuctionActivity.class);
                final Intent intentRe = new Intent(getActivity(), AddNewWorkActivity.class);

                intent.putExtra("work_name", s.get_work_name());
                intent.putExtra("status", s.get_status());
                intent.putExtra("duration", s.get_duration());
                intent.putExtra("deadline", s.get_deadline());
                intent.putExtra("time", ( s.get_time() +"  "+s.get_date()));
                intent.putExtra("bidsID", s.get_bidsID());

                intent.putExtra("bidsLastValue", s.get_bidsLastValue());
                intent.putExtra("bidsLastUser", s.get_bidsLastUser());
                intent.putExtra("bidsCount", s.get_bidsCount());
                intent.putExtra("workProgress", s.get_workProgress());

                intent.putExtra("myWork", "0");

                if(s.get_status().equals("Status : unauctioned") || s.get_status().equals("Status : uncompleted"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Want you delete this work ?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id)
                                {
                                            dialog.cancel();
                                }
                            })
                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id)
                                {
                                    db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(s.get_bidsID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                            {
                                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                String childKey = childSnapshot.getKey();
                                                if ( s.get_status().equals("Status : unauctioned") || s.get_status().equals("Status : uncompleted") )
                                                    db.child("groups").child(groupID).child("works").child("todo").child(childKey).setValue(null);
                                            }
                                            dialog.cancel();
                                            /*for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                            {
                                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                String childKey = childSnapshot.getKey();
                                                db.child("groups").child(groupID).child("works").child("todo").child(childKey).setValue(null);
                                            }
                                            intentRe.putExtra("work_name", s.get_work_name());
                                            startActivity(intentRe);*/

                                        }

                                        @Override public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    if(!s.get_status().equals("Status : done"))
                    {
                        startActivity(intent);
                    }
                }
            }

        });

        return rootView;
    }

}