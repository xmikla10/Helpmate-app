package com.flatmate.flatmate.Activity;

import android.app.AlarmManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.PendingIntent;


import com.flatmate.flatmate.Firebase.FirebaseHelperAuction;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyWorks;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.GraphUser;
import com.flatmate.flatmate.Firebase.Months;
import com.flatmate.flatmate.Firebase.NewBid;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.AlarmReceiver;
import com.flatmate.flatmate.Other.BidPopUp;
import com.flatmate.flatmate.Other.CustomAdapterAuction;
import com.flatmate.flatmate.Other.CustomAdapterMyWorks;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.Other.WorkDoneReceiver;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class AuctionActivity extends AppCompatActivity
{

    private FirebaseAuth firebaseAuth;
    DatabaseReference db;
    private String groupID;
    public String bidsID;

    String userID;
    String bid;
    String userName;
    String userEmail;
    String bidsLastValue;
    String bidsLastUser;
    String bidsCount;
    String bidsAddUser;
    String childKey;
    String statusToShow;
    String lastUser;
    String actualMonth;

    Boolean bidsLastIsNull;
    FirebaseHelperAuction helper;
    CustomAdapterAuction adapter;
    NewWork newWork;
    ListView lv;
    Integer bidsLast;
    Integer bidsActual;
    Integer groupMembersCount;
    boolean evaluation;

    String seekEditable;
    boolean setSeekEditable;

    ArrayList<NewWork> a =new ArrayList<>();
    public static final String TAG = AuctionActivity.class.getSimpleName();
    public static final String SELECTED_ADD_KEY = "bid_result";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_layout);
        Bundle extras = getIntent().getExtras();

        bidsLast = 0;
        bidsActual = 0;
        groupMembersCount = 0;

        if (extras != null)
        {
            String work_name = extras.getString("work_name");
            String status = extras.getString("status");
            String duration = extras.getString("duration");
            String deadline = extras.getString("deadline");
            String time = extras.getString("time");
            String myWork = extras.getString("myWork");
            bidsID = extras.getString("bidsID");
            seekEditable = extras.getString("workProgress");

            bidsLastUser = extras.getString("bidsLastUser");
            bidsLastValue = extras.getString("bidsLastValue");
            bidsCount = extras.getString("bidsCount");

            TextView work_name1 = (TextView) findViewById(R.id.auctionWorkName);
            TextView status1 = (TextView) findViewById(R.id.auctionStatus);
            TextView duration1 = (TextView) findViewById(R.id.auctionDuration);
            TextView time1 = (TextView) findViewById(R.id.auctionDeadline);
            TextView deadline1 = (TextView) findViewById(R.id.auctionTime);

            String pom = "";
            if( work_name.length() > 20)
            {
                char[] charArray = work_name.toCharArray();
                for( int i = 0; i < 20; i++)
                {
                    pom = pom + String.valueOf(charArray[i]);
                }
                pom = pom + "...";
            }
            else
                pom = work_name;

            work_name1.setText(pom);
            status1.setText(status);
            duration1.setText(duration);
            deadline1.setText(deadline);
            time1.setText(time);

            if ( myWork.equals("1"))
            {
                TextView auctiontext = (TextView) findViewById(R.id.textViewAuctionText);
                ListView bids = (ListView) findViewById(R.id.listViewAuction);
                LinearLayout bar = (LinearLayout) findViewById(R.id.seekBar2);
                LinearLayout lay = (LinearLayout) findViewById(R.id.layMyWorks);
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_bid);
                SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarAuction);

                auctiontext.setVisibility(View.GONE);
                bids.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);
                lay.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                setSeekEditable = true;

            }
            else
                setSeekEditable = false;

            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarAuction);
            final LinearLayout lay2 = (LinearLayout) findViewById(R.id.layMyWorks);
            seekBar.setProgress(Integer.valueOf(seekEditable));

            seekBar.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (setSeekEditable == true)
                    {
                        return false;
                    }
                    else
                        return true;
                }
            });

        }

        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperAuction(db);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();

        adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
        lv = (ListView) findViewById(R.id.listViewAuction);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {

            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                bidsExistListener(bidsID);

                db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            childKey = childSnapshot.getKey();
                            Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                            statusToShow = value.get("_status").toString();
                            lastUser = value.get("_bidsLastUserName").toString();
                        }

                        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarAuction);
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                        {
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                            public void onStartTrackingTouch(SeekBar seekBar) {}
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                            {
                                Map newWorkData = new HashMap();
                                newWorkData.put("_workProgress", String.valueOf(progress));
                                db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newWorkData);
                            }
                        });

                        if ( !statusToShow.equals("Status : auctioning"))
                        {
                            endOfEvaluation(lastUser, statusToShow);
                        }

                        db.child("groups").child(groupID).child("bids").child(bidsID).addChildEventListener(new ChildEventListener() {
                            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                ProgressBar mProgress= (ProgressBar) findViewById(R.id.loadingProgressBar);
                                mProgress.setVisibility(View.GONE);
                                adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
                                lv.setAdapter(adapter);
                            }
                            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                ProgressBar mProgress= (ProgressBar) findViewById(R.id.loadingProgressBar);
                                mProgress.setVisibility(View.GONE);
                                adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
                                lv.setAdapter(adapter);
                            }
                            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();

                bidsExistListener(bidsID);
                db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            childKey = childSnapshot.getKey();
                            Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                            statusToShow = value.get("_status").toString();
                            lastUser = value.get("_bidsLastUserName").toString();
                        }

                        if ( !statusToShow.equals("Status : auctioning"))
                        {
                            endOfEvaluation(lastUser, statusToShow);
                        }

                        db.child("groups").child(groupID).child("bids").child(bidsID).addChildEventListener(new ChildEventListener() {
                            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                ProgressBar mProgress= (ProgressBar) findViewById(R.id.loadingProgressBar);
                                mProgress.setVisibility(View.GONE);
                                adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
                                lv.setAdapter(adapter);
                            }
                            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                ProgressBar mProgress= (ProgressBar) findViewById(R.id.loadingProgressBar);
                                mProgress.setVisibility(View.GONE);
                                adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
                                lv.setAdapter(adapter);
                            }
                            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });

                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}

        });

        lv.setAdapter(adapter);

    }

    String completedName;
    String completedEmail;
    String completedWorkName;
    String completedCredits;

    public void onClickButtonWorkDone(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to finish this work?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                            {

                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                {
                                    childKey = childSnapshot.getKey();
                                    Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                                    completedCredits = value.get("_bidsLastValue").toString();
                                    completedEmail = value.get("_bidsLastUser").toString();
                                    completedWorkName = value.get("_work_name").toString();
                                    completedName = value.get("_bidsLastUserName").toString();
                                    // date
                                }

                                NewWork completedWork = new NewWork();
                                completedWork.set_work_name(completedWorkName);
                                completedWork.set_bidsLastUserName(completedName);
                                completedWork.set_bidsLastUser(completedEmail);
                                completedWork.set_credits(completedCredits + " credits");
                                completedWork.set_workProgress("6");
                                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                completedWork.set_date(date);

                                db.child("groups").child(groupID).child("completed").child("works").push().setValue(completedWork);

                                Map newCompletedData = new HashMap();

                                newCompletedData.put("_userEmail", "null");
                                newCompletedData.put("_workProgress", "6");
                                newCompletedData.put("_status", "Status : done");

                                db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newCompletedData);

                                final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                                Calendar cal = Calendar.getInstance();
                                final String currentDateandTime = dateFormat.format(cal.getTime());
                                Date datePlus = null;

                                try
                                {
                                    datePlus = dateFormat.parse(currentDateandTime);
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }

                                cal.setTime(datePlus);
                                cal.add(Calendar.HOUR, 24);

                                Map dateData = new HashMap();
                                dateData.put("_deadline", dateFormat.format(cal.getTime()));
                                db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(dateData);


                                int _id = (int) System.currentTimeMillis();

                                Intent intent = new Intent(AuctionActivity.this, WorkDoneReceiver.class);
                                intent.putExtra("alarmId", _id);
                                intent.putExtra("bidsID", bidsID);
                                intent.putExtra("groupID", groupID);
                                intent.putExtra("childKey", childKey);

                                PendingIntent pending = PendingIntent.getBroadcast(AuctionActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                cal = null;

                                Date actDate= new Date();
                                Calendar actCal = Calendar.getInstance();
                                actCal.setTime(actDate);
                                Integer monthInInt = actCal.get(Calendar.MONTH) + 1;
                                actualMonth = getMonth(monthInInt);

                                db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("control").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        String graphMonth = null;

                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                        {
                                            childKey = childSnapshot.getKey();
                                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                            graphMonth = value.get("_month").toString();
                                        }

                                        if (graphMonth.equals("null"))
                                        {
                                            db.child("groups").child(groupID).child("graph").child("months").child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override public void onDataChange(DataSnapshot dataSnapshot)
                                                {
                                                    String members = null;
                                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                                    {
                                                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                        members = value.get("_membersCount").toString();
                                                        groupMembersCount = Integer.valueOf(members);
                                                    }

                                                    Date actDate= new Date();
                                                    Calendar actCal = Calendar.getInstance();
                                                    actCal.setTime(actDate);
                                                    Integer year = actCal.get(Calendar.YEAR);

                                                    Map newData = new HashMap();
                                                    newData.put("_month", year);
                                                    newData.put("_membersCount", members);
                                                    db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("control").child(childKey).updateChildren(newData);


                                                    //namapovat clenov pre graf
                                                    db.child("user").child("groups").child("members").child(groupID).addChildEventListener(new ChildEventListener()
                                                    {
                                                        Integer memC = 0;
                                                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
                                                        {
                                                            Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                                            String memberName = value.get("_user_name").toString();
                                                            String memberEmail = value.get("_user_email").toString();
                                                            String memberID = value.get("_user_ID").toString();

                                                            GraphUser graphUser = new GraphUser();
                                                            graphUser.set_ID(memberID);
                                                            graphUser.set_name(memberName);
                                                            graphUser.set_email(memberEmail);
                                                            graphUser.set_credits("0");
                                                            memC++;

                                                            db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("users").push().setValue(graphUser);

                                                            if ( memC == groupMembersCount)
                                                            {
                                                                addToGraph();
                                                            }
                                                        }
                                                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                                                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                                        @Override public void onCancelled(DatabaseError databaseError) {}
                                                    });
                                                }
                                                @Override public void onCancelled(DatabaseError databaseError) {}});
                                        }
                                        else
                                        {
                                            Date actDate= new Date();
                                            Calendar actCal = Calendar.getInstance();
                                            actCal.setTime(actDate);
                                            Integer year = actCal.get(Calendar.YEAR);

                                            Integer yearInGraPH = Integer.valueOf(graphMonth);

                                            if ( year > yearInGraPH)
                                            {
                                                db.child("groups").child(groupID).child("graph").child("months").child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                                                    {
                                                        String members = null;
                                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                                        {
                                                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                            members = value.get("_membersCount").toString();
                                                            groupMembersCount = Integer.valueOf(members);
                                                        }

                                                        Date actDate= new Date();
                                                        Calendar actCal = Calendar.getInstance();
                                                        actCal.setTime(actDate);
                                                        Integer year = actCal.get(Calendar.YEAR);

                                                        Map newData = new HashMap();
                                                        newData.put("_month", year);
                                                        newData.put("_membersCount", members);
                                                        db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("control").child(childKey).updateChildren(newData);


                                                        db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("users").setValue(null);
                                                        db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("users").removeValue();

                                                        //namapovat clenov pre graf
                                                        db.child("user").child("groups").child("members").child(groupID).addChildEventListener(new ChildEventListener()
                                                        {
                                                            Integer memC = 0;
                                                            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
                                                            {
                                                                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                                                String memberName = value.get("_user_name").toString();
                                                                String memberEmail = value.get("_user_email").toString();
                                                                String memberID = value.get("_user_ID").toString();

                                                                GraphUser graphUser = new GraphUser();
                                                                graphUser.set_ID(memberID);
                                                                graphUser.set_name(memberName);
                                                                graphUser.set_email(memberEmail);
                                                                graphUser.set_credits("0");
                                                                memC++;

                                                                db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("users").push().setValue(graphUser);

                                                                if ( memC == groupMembersCount)
                                                                {
                                                                    addToGraph();
                                                                }
                                                            }
                                                            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                                                            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                                            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                                            @Override public void onCancelled(DatabaseError databaseError) {}
                                                        });
                                                    }
                                                    @Override public void onCancelled(DatabaseError databaseError) {}});
                                            }
                                            else
                                                addToGraph();
                                        }

                                    }
                                    @Override public void onCancelled(DatabaseError databaseError) {}});
                            }
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                       AuctionActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addToGraph()
    {
        //zistím userID , podla toho ro zoradím a pridím do toho
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        db = FirebaseDatabase.getInstance().getReference();

        db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("users").orderByChild("_ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override public void onDataChange(DataSnapshot dataSnapshot)
        {
            Integer credits = 0;
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
            {
                childKey = childSnapshot.getKey();
                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                String cred = value.get("_credits").toString();
                credits = Integer.valueOf(cred);
                Log.d("Control - childKey", "childKey");

            }

            //tu chcem už pridať
            credits = credits + Integer.valueOf(completedCredits);
            Map newData = new HashMap();
            newData.put("_credits", credits);
            db.child("groups").child(groupID).child("graph").child("months").child(actualMonth).child("users").child(childKey).updateChildren(newData);


        }
            @Override public void onCancelled(DatabaseError databaseError) {}});
    }

    public String getMonth(Integer month)
    {
        String monthString;
        switch (month)
        {
            case 1:  monthString = "January";break;
            case 2:  monthString = "February";break;
            case 3:  monthString = "March";break;
            case 4:  monthString = "April";break;
            case 5:  monthString = "May";break;
            case 6:  monthString = "June";break;
            case 7:  monthString = "July";break;
            case 8:  monthString = "August";break;
            case 9:  monthString = "September";break;
            case 10: monthString = "October";break;
            case 11: monthString = "November";break;
            case 12: monthString = "December";break;
            default: monthString = "Invalid month";break;
        }

        return monthString;
    }

    public void endOfEvaluation(String lastUser, String status)
    {
        TextView auctiontext = (TextView) findViewById(R.id.textViewAuctionText);
        ListView bids = (ListView) findViewById(R.id.listViewAuction);
        LinearLayout bar = (LinearLayout) findViewById(R.id.seekBar2);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_bid);

        TextView textViewTaskStatus = (TextView) findViewById(R.id.textViewTaskStatus);
        TextView auctionStatus = (TextView) findViewById(R.id.auctionStatus);
        TextView auctionTime = (TextView) findViewById(R.id.auctionTime);

        textViewTaskStatus.setText("Who won?");
        textViewTaskStatus.setTextSize(17);
        auctionStatus.setText(status);
        auctionTime.setText(lastUser);

        auctiontext.setVisibility(View.GONE);
        bids.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);
    }

    public void bidsExistListener(final String bidID)
    {
        db.child("groups").child(groupID).child("bids").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(bidID).exists()) {
                    ProgressBar mProgress= (ProgressBar) findViewById(R.id.loadingProgressBar);
                    mProgress.setVisibility(View.GONE);
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void showAddBid(View view)
    {
        Intent intent = new Intent(this, BidPopUp.class);
        intent.putExtra("bidsID", bidsID);
        startActivityForResult( intent, BidPopUp.BID_POPUP_FINISHED);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BidPopUp.BID_POPUP_FINISHED:

                if ( data == null)
                    return;
                String[] split = data.getStringExtra(AuctionActivity.SELECTED_ADD_KEY).split("-");
                bid = Arrays.toString(split).replace("[", "").replace("]", "");

                db = FirebaseDatabase.getInstance().getReference();
                helper = new FirebaseHelperAuction(db);
                firebaseAuth = FirebaseAuth.getInstance();
                userID = firebaseAuth.getCurrentUser().getUid().toString();

                db.child("groups").child(groupID).child("graph").child("months").child("members").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        final String members = value.get("_membersCount").toString();

                    db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                            {
                                childKey = childSnapshot.getKey();
                                Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                                bidsLastValue = value.get("_bidsLastValue").toString();
                                bidsCount = value.get("_bidsCount").toString();
                                bidsAddUser = value.get("_bidsAddUsers").toString();
                                bidsLastUser = value.get("_bidsLastUser").toString();
                            }

                            if(!bidsLastValue.equals("null") && !bid.equals("not interested"))
                            {
                                bidsLast = Integer.valueOf(bidsLastValue);
                                bidsActual = Integer.valueOf(bid);
                                bidsLastIsNull = false;
                            }
                            else
                            {
                                bidsLastIsNull = true;
                            }


                            if (bidsLast <= bidsActual && bidsLastIsNull == false)
                            {
                                Toast.makeText(getBaseContext(), "Please, enter a lesser bid then a last user", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
                                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                        userName = value.get("_name").toString();
                                        groupID = value.get("_group").toString();
                                        Map newWorkData = new HashMap();

                                        if( bidsAddUser.equals("null"))
                                        {
                                            Integer bidsC = 0 ;
                                            bidsC++;
                                            bidsCount = String.valueOf(bidsC);
                                            newWorkData.put("_bidsCount", bidsCount);
                                            newWorkData.put("_bidsAddUsers", userID + ",");
                                        }
                                        else
                                        {
                                            if (bidsAddUser.indexOf(userID) != -1)
                                            {
                                                System.out.println("------->" + "Vyslo to");
                                            }
                                            else
                                            {
                                                Integer bidsC = Integer.valueOf(bidsCount);
                                                bidsC++;
                                                bidsCount = String.valueOf(bidsC);
                                                newWorkData.put("_bidsCount", bidsCount);
                                                newWorkData.put("_bidsAddUsers", bidsAddUser + userID + ",");
                                            }
                                        }

                                        if(!bid.equals("not interested"))
                                        {
                                            bidsLastValue = bid;
                                            newWorkData.put("_bidsLastValue", bidsLastValue);
                                            newWorkData.put("_bidsLastUser", userEmail);
                                            newWorkData.put("_bidsLastUserName", userName);

                                            evaluation = true;
                                        }

                                        db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newWorkData);

                                        NewBid newbid = new NewBid();

                                        if(bid.equals("not interested"))
                                        {
                                            newbid.set_credits(bid);
                                        }
                                        else
                                            newbid.set_credits(bid + " credits");

                                        newbid.set_userName(userName);
                                        helper.save(newbid, bidsID, groupID);

                                        if ( Integer.valueOf(members) <= Integer.valueOf(bidsCount))
                                        {
                                            Map newEvaluationData = new HashMap();
                                            if ( evaluation == true)
                                            {
                                                newEvaluationData.put("_userEmail", userEmail);
                                            }
                                            else
                                                newEvaluationData.put("_userEmail", bidsLastUser);

                                            newEvaluationData.put("_status", "Status : in progress");

                                            db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newEvaluationData);
                                        }

                                        finish();
                                        startActivity(getIntent());
                                    }
                                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                    @Override public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                        }
                        @Override public void onCancelled(DatabaseError databaseError) {}
                        });

                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
                break;

            default:
                Log.d(TAG, "onActivityResult: uknown request code " + requestCode);
        }
    }
}
