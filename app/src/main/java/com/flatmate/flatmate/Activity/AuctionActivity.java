package com.flatmate.flatmate.Activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DiscretePathEffect;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Space;
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
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.BidPopUp;
import com.flatmate.flatmate.Other.CustomAdapterAuction;
import com.flatmate.flatmate.Other.CustomAdapterMyWorks;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.Other.MyStatus;
import com.flatmate.flatmate.Other.SetNotification;
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
import java.util.Locale;
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
    public String bidsIDUpdateSeek;
    public String toastCompletedCredits;

    String userID;
    String bid;
    String userName;
    String userEmail;
    String bidsLastValue;
    String bidsLastUser;
    String bidsCount;
    String bidsAddUser;
    String childKey;
    String childKeyFork;
    String childKeyUpdateSeek;
    String statusToShow;
    String lastUser;
    String actualMonth;
    String work_name;

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
    public SeekBar seekBar2;

    ArrayList<NewWork> a =new ArrayList<>();
    public static final String TAG = AuctionActivity.class.getSimpleName();
    public static final String SELECTED_ADD_KEY = "bid_result";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setLocale();

        setContentView(R.layout.activity_auction_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyGroups);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();

        bidsLast = 0;
        bidsActual = 0;
        groupMembersCount = 0;

        if (extras != null)
        {
            String work_name = extras.getString("work_name");
            String status = extras.getString("status");

            MyStatus statusC = new MyStatus();
            String statusInString = statusC.setStatus( status, AuctionActivity.this);

            String duration = extras.getString("duration");
            String deadline = extras.getString("deadline");
            String time = extras.getString("time");
            String myWork = extras.getString("myWork");
            bidsID = extras.getString("bidsID");
            bidsIDUpdateSeek = extras.getString("bidsID");
            seekEditable = extras.getString("workProgress");

            bidsLastUser = extras.getString("bidsLastUser");
            bidsLastValue = extras.getString("bidsLastValue");
            bidsCount = extras.getString("bidsCount");

            TextView work_name1 = (TextView) findViewById(R.id.auctionWorkName);
            TextView status1 = (TextView) findViewById(R.id.auctionStatus);
            TextView duration1 = (TextView) findViewById(R.id.auctionDuration);
            TextView time1 = (TextView) findViewById(R.id.auctionDeadline);
            TextView deadline1 = (TextView) findViewById(R.id.auctionTime);
            TextView circleLetter = (TextView) findViewById(R.id.circleLetter);
            GradientDrawable gd = (GradientDrawable) circleLetter.getBackground();
            Space spaceUnderCircle = (Space) findViewById(R.id.spaceUnderCircle);
            Space spaceAuction = (Space) findViewById(R.id.spaceAuction);

            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth();

            if ( width < 720 && width > 680)
            {
                spaceUnderCircle.getLayoutParams().width=53;
                spaceAuction.getLayoutParams().width=15;
            }

            if ( width <= 680 )
            {
                spaceUnderCircle.getLayoutParams().width=45;
                spaceAuction.getLayoutParams().width=10;
            }


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

            String firstLetter = work_name.substring(0,1).toUpperCase();

            switch (firstLetter)
            {
                case "A":case "B":case "C":case "D":
                gd.setColor(Color.RED); break;
                case "E":case "F":case "G":case "H":
                gd.setColor(Color.parseColor("#0091EA")); break;
                case "I":case "J":case "K":case "L":
                gd.setColor(Color.parseColor("#689F38")); break;
                case "M":case "N":case "O":case "P":
                gd.setColor(Color.parseColor("#E64A19")); break;
                case "Q":case "R":case "S":case "T":
                gd.setColor(Color.parseColor("#7C4DFF")); break;
                case "U":case "V":case "W":case "X":
                gd.setColor(Color.parseColor("#FF80AB")); break;
                case "Y":case "Z":
                gd.setColor(Color.parseColor("#EF6C00")); break;
                default:
                    gd.setColor(Color.parseColor("#FB7B0A")); break;

            }

            circleLetter.setText(firstLetter);

            work_name1.setText(pom);
            status1.setText(statusInString);
            duration1.setText(duration);
            deadline1.setText(deadline);
            time1.setText(time);

            if ( myWork.equals("1"))
            {
                TextView auctiontext = (TextView) findViewById(R.id.textViewAuctionText);
                ListView bids = (ListView) findViewById(R.id.listViewAuction);
                LinearLayout bar = (LinearLayout) findViewById(R.id.seekBar2);
                LinearLayout seek = (LinearLayout) findViewById(R.id.layoutForSeekBar);
                LinearLayout lay = (LinearLayout) findViewById(R.id.layMyWorks);
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_bid);

                auctiontext.setVisibility(View.GONE);
                bids.setVisibility(View.GONE);
                seek.setVisibility(View.VISIBLE);
                bar.setVisibility(View.VISIBLE);
                lay.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                setSeekEditable = true;

            }
            else
                setSeekEditable = false;

        }

        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperAuction(db);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();

        adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
        lv = (ListView) findViewById(R.id.listViewAuction);


        LinearLayout mLayout = (LinearLayout) findViewById(R.id.layoutForSeekBar);
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        seekBar2 = new SeekBar(this);
        seekBar2.setLayoutParams(lparams);
        seekBar2.setProgress(1);
        seekBar2.setMax(6);
        seekBar2.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        seekBar2.setVisibility(View.VISIBLE);
        seekBar2.setProgress(Integer.valueOf(seekEditable));
        mLayout.addView(seekBar2);

        seekBar2.setOnTouchListener(new View.OnTouchListener(){
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



        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {

            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                bidsExistListener(bidsID);

                if(groupID.length() != 0)
                {
                    seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                    {
                        public void onStopTrackingTouch(SeekBar seekBar2) {}
                        public void onStartTrackingTouch(SeekBar seekBar2) {}
                        public void onProgressChanged(final SeekBar seekBar2, int progress, boolean fromUser)
                        {
                            db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsIDUpdateSeek).addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    String userCanUpdateSeek = "";

                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                    {
                                        childKeyUpdateSeek = childSnapshot.getKey();
                                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                        userCanUpdateSeek = value.get("_userEmail").toString();
                                    }

                                    if( userCanUpdateSeek.equals(userEmail))
                                    {
                                        Map newWorkData = new HashMap();
                                        newWorkData.put("_workProgress", String.valueOf(seekBar2.getProgress()));
                                        db.child("groups").child(groupID).child("works").child("todo").child(childKeyUpdateSeek).updateChildren(newWorkData);
                                    }

                                }
                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    });

                    db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsIDUpdateSeek).addChildEventListener(new ChildEventListener()
                    {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
                        {
                            String userCanUpdateSeek = "";
                            String seekValue = "";

                            Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                            userCanUpdateSeek = value.get("_userEmail").toString();
                            seekValue = value.get("_workProgress").toString();

                            if( !userCanUpdateSeek.equals(userEmail))
                                seekBar2.setProgress(Integer.valueOf(seekValue));

                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });


                    db.child("groups").child(groupID).child("works").child("todo").addChildEventListener(new ChildEventListener()
                    {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
                        {
                            Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                            String progressValue = value.get("_workProgress").toString();
                            String bidsC = value.get("_bidsCount").toString();
                            String evaluationEmailUser = value.get("_userEmail").toString();
                            String status= value.get("_status").toString();

                            MyStatus statusC = new MyStatus();

                            String statusInString = statusC.setStatus( status, AuctionActivity.this);
                            status = statusInString;

                            if(status.equals(getString(R.string.status_progress)) || status.equals(getString(R.string.status_done)) || status.equals(getString(R.string.status_unauctioned)) )
                            {

                                if (!evaluationEmailUser.equals("null")) {
                                    ListView bids = (ListView) findViewById(R.id.listViewAuction);
                                    if (bids.getVisibility() != View.INVISIBLE) {
                                        finish();
                                    }
                                }

                                if(status.equals(getString(R.string.status_unauctioned)))
                                    finish();
                            }
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            childKeyFork = childSnapshot.getKey();
                            Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                            statusToShow = value.get("_status").toString();
                            MyStatus statusC = new MyStatus();

                            String statusInString = statusC.setStatus( statusToShow, AuctionActivity.this);
                            statusToShow = statusInString;

                            lastUser = value.get("_bidsLastUserName").toString();
                        }

                        if ( !statusToShow.equals(getString(R.string.status_auctioning)))
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

                bidsExistListener(bidsID);
                db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String seekValue = null;
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                            statusToShow = value.get("_status").toString();
                            lastUser = value.get("_bidsLastUserName").toString();

                            MyStatus statusC = new MyStatus();
                            String statusInString = statusC.setStatus( statusToShow, AuctionActivity.this);
                            statusToShow = statusInString;
                        }

                        if ( !statusToShow.equals(getString(R.string.status_auctioning)))
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

        /*db.child("groups").child(groupID).child("works").child("todo").child(bidsID).orderByChild("_bidsID").equalTo(bidsID).addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String seekValue = value.get("_workProgress").toString();
                SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarAuction);
                seekBar.setProgress(Integer.valueOf(seekValue));
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String seekValue = value.get("_workProgress").toString();
                SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarAuction);
                seekBar.setProgress(Integer.valueOf(seekValue));
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });*/

    }

    public void onClickButtonCancelWork(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_cancel_work)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                String cancelChildKey;
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                {
                                    cancelChildKey = childSnapshot.getKey();
                                    Map newData = new HashMap();
                                    newData.put("_status", "5");
                                    db.child("groups").child(groupID).child("works").child("todo").child(cancelChildKey).updateChildren(newData);
                                }

                            }
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                        AuctionActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    String completedName;
    String completedEmail;
    String completedWorkName;
    String completedCredits;

    public void onClickButtonWorkDone(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_finish_work)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                            {

                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                {
                                    childKeyFork = childSnapshot.getKey();
                                    Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                                    completedCredits = value.get("_bidsLastValue").toString();
                                    toastCompletedCredits = value.get("_bidsLastValue").toString();
                                    completedEmail = value.get("_bidsLastUser").toString();
                                    completedWorkName = value.get("_work_name").toString();
                                    completedName = value.get("_bidsLastUserName").toString();
                                    // date
                                }

                                NewWork completedWork = new NewWork();
                                completedWork.set_work_name(completedWorkName);
                                completedWork.set_bidsLastUserName(completedName);
                                completedWork.set_bidsLastUser(completedEmail);
                                completedWork.set_credits(completedCredits);
                                completedWork.set_workProgress("6");
                                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                completedWork.set_date(date);

                                db.child("groups").child(groupID).child("completed").child("works").push().setValue(completedWork);

                                SetNotification set = new SetNotification();
                                set.Set(groupID, 5, completedWorkName, bidsID, "");

                                Map newCompletedData = new HashMap();

                                newCompletedData.put("_userEmail", "null");
                                newCompletedData.put("_workProgress", "6");
                                newCompletedData.put("_status", "3");

                                db.child("groups").child(groupID).child("works").child("todo").child(childKeyFork).updateChildren(newCompletedData);

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
                                cal.add(Calendar.HOUR, 6);

                                Map dateData = new HashMap();
                                dateData.put("_deadline", dateFormat.format(cal.getTime()));
                                db.child("groups").child(groupID).child("works").child("todo").child(childKeyFork).updateChildren(dateData);


                                int _id = (int) System.currentTimeMillis();

                                Intent intent = new Intent(AuctionActivity.this, WorkDoneReceiver.class);

                                intent.putExtra("alarmId", _id);
                                intent.putExtra("bidsID", bidsID);
                                intent.putExtra("groupID", groupID);
                                intent.putExtra("childKey", childKeyFork);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                                                    db.child("user").child("groups").child("members").child(groupID).child("members").addChildEventListener(new ChildEventListener()
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
                                                        db.child("user").child("groups").child("members").child(groupID).child("members").addChildEventListener(new ChildEventListener()
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

                                if (Integer.valueOf(toastCompletedCredits) == 1 )
                                    Toast.makeText(getBaseContext(), getString(R.string.congrat) + toastCompletedCredits + getString(R.string.credits), Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getBaseContext(), getString(R.string.congrat) + toastCompletedCredits + getString(R.string.credits), Toast.LENGTH_SHORT).show();
                            }
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });

                       AuctionActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
            }

            //tu chcem už pridať
            credits = credits + Integer.valueOf(completedCredits);
            Map newData = new HashMap();
            newData.put("_credits", credits.toString());
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
        LinearLayout seek = (LinearLayout) findViewById(R.id.layoutForSeekBar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_bid);

        TextView textViewTaskStatus = (TextView) findViewById(R.id.textViewTaskStatus);
        TextView auctionStatus = (TextView) findViewById(R.id.auctionStatus);
        TextView auctionTime = (TextView) findViewById(R.id.auctionTime);

        MyStatus statusC = new MyStatus();

        if ( status.equals(getString(R.string.status_done)))
        {
            textViewTaskStatus.setText(R.string.work_completed_by);
            textViewTaskStatus.setTextSize(16);
        }
        else
        {
            textViewTaskStatus.setText(R.string.who_won);
            textViewTaskStatus.setTextSize(16);
        }
        auctionStatus.setText(status);
        auctionTime.setText(lastUser);

        auctiontext.setVisibility(View.GONE);
        bids.setVisibility(View.INVISIBLE);
        seek.setVisibility(View.VISIBLE);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

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

                if ( isOnline() == false)
                {
                    Toast.makeText(getBaseContext(), R.string.internet_connection, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String[] split = data.getStringExtra(AuctionActivity.SELECTED_ADD_KEY).split("-");
                    bid = Arrays.toString(split).replace("[", "").replace("]", "");

                    db = FirebaseDatabase.getInstance().getReference();
                    helper = new FirebaseHelperAuction(db);
                    firebaseAuth = FirebaseAuth.getInstance();
                    userID = firebaseAuth.getCurrentUser().getUid().toString();

                    db.child("groups").child(groupID).child("graph").child("months").child("members").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                            final String members = value.get("_membersCount").toString();

                            db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        childKeyFork = childSnapshot.getKey();
                                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                        bidsLastValue = value.get("_bidsLastValue").toString();
                                        bidsCount = value.get("_bidsCount").toString();
                                        bidsAddUser = value.get("_bidsAddUsers").toString();
                                        bidsLastUser = value.get("_bidsLastUser").toString();
                                        work_name = value.get("_work_name").toString();
                                    }

                                    if (!bidsLastValue.equals("null") && !bid.equals("not interested")) {
                                        bidsLast = Integer.valueOf(bidsLastValue);
                                        bidsActual = Integer.valueOf(bid);
                                        bidsLastIsNull = false;
                                    } else {
                                        bidsLastIsNull = true;
                                    }


                                    if (bidsLast <= bidsActual && bidsLastIsNull == false) {
                                        Toast.makeText(getBaseContext(), R.string.lesser_bid, Toast.LENGTH_SHORT).show();
                                    } else {
                                        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                                userName = value.get("_name").toString();
                                                groupID = value.get("_group").toString();
                                                Map newWorkData = new HashMap();

                                                if (bidsAddUser.equals("null")) {
                                                    Integer bidsC = 0;
                                                    bidsC++;
                                                    bidsCount = String.valueOf(bidsC);
                                                    newWorkData.put("_bidsCount", bidsCount);
                                                    newWorkData.put("_bidsAddUsers", userID + ",");
                                                } else {
                                                    if (bidsAddUser.indexOf(userID) != -1) {
                                                        System.out.println("" + "");
                                                    } else {
                                                        Integer bidsC = Integer.valueOf(bidsCount);
                                                        bidsC++;
                                                        bidsCount = String.valueOf(bidsC);
                                                        newWorkData.put("_bidsCount", bidsCount);
                                                        newWorkData.put("_bidsAddUsers", bidsAddUser + userID + ",");
                                                    }
                                                }

                                                if (!bid.equals("not interested")) {
                                                    bidsLastValue = bid;
                                                    newWorkData.put("_bidsLastValue", bidsLastValue);
                                                    newWorkData.put("_bidsLastUser", userEmail);
                                                    newWorkData.put("_bidsLastUserName", userName);

                                                    evaluation = true;
                                                }

                                                db.child("groups").child(groupID).child("works").child("todo").child(childKeyFork).updateChildren(newWorkData);

                                                NewBid newbid = new NewBid();

                                                if (bid.equals(getString(R.string.not_interested))) {
                                                    newbid.set_credits(bid);
                                                } else
                                                    newbid.set_credits(bid);

                                                newbid.set_userName(userName);
                                                helper.save(newbid, bidsID, groupID);

                                                //vyhodnotenie
                                                if (Integer.valueOf(members) <= Integer.valueOf(bidsCount)) {
                                                    Map newEvaluationData = new HashMap();
                                                    Map newEvaluationData2 = new HashMap();
                                                    String notifEmail;

                                                    if (evaluation == true) {
                                                        newEvaluationData.put("_userEmail", userEmail);
                                                        notifEmail = userEmail;
                                                    } else {
                                                        newEvaluationData.put("_userEmail", bidsLastUser);
                                                        notifEmail = bidsLastUser;
                                                    }

                                                    if (bidsLastUser.equals("null") && evaluation != true) {
                                                        newEvaluationData2.put("_status", "4");
                                                        db.child("groups").child(groupID).child("works").child("todo").child(childKeyFork).updateChildren(newEvaluationData2);
                                                        SetNotification set = new SetNotification();
                                                        set.Set(groupID, 8, work_name, bidsID, "");
                                                    } else {
                                                        newEvaluationData.put("_status", "2");
                                                        db.child("groups").child(groupID).child("works").child("todo").child(childKeyFork).updateChildren(newEvaluationData);
                                                        SetNotification set1 = new SetNotification();
                                                        SetNotification set2 = new SetNotification();
                                                        set1.Set(groupID, 6, work_name, bidsID, notifEmail);
                                                        set2.Set(groupID, 4, work_name, bidsID, notifEmail);
                                                    }

                                                    //Intent intent = new Intent(AuctionActivity.this, MainActivity.class);
                                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    //startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                                } else {
                                                    finish();
                                                    startActivity(getIntent());
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
                }
                break;

            default:
                Log.d(TAG, "onActivityResult: uknown request code " + requestCode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else
        {
            System.out.println("---------> " + "Nieje pripojenie na internet");
            return false;
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private void setLocale()
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("language", null);

        if (restoredText != null)
        {
            Locale myLocale = new Locale(restoredText);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
        else
        {
            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

    }
}
