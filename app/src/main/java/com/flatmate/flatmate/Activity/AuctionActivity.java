package com.flatmate.flatmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.FirebaseHelperAuction;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyWorks;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.NewBid;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.BidPopUp;
import com.flatmate.flatmate.Other.CustomAdapterAuction;
import com.flatmate.flatmate.Other.CustomAdapterMyWorks;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class AuctionActivity extends AppCompatActivity
{

    private FirebaseAuth firebaseAuth;
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
    Boolean bidsLastIsNull;
    DatabaseReference db;
    FirebaseHelperAuction helper;
    CustomAdapterAuction adapter;
    NewWork newWork;
    ListView lv;
    Integer bidsLast;
    Integer bidsActual;

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

        if (extras != null)
        {
            String work_name = extras.getString("work_name");
            String status = extras.getString("status");
            String duration = extras.getString("duration");
            String deadline = extras.getString("deadline");
            String time = extras.getString("time");
            String myWork = extras.getString("myWork");
            bidsID = extras.getString("bidsID");

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

                auctiontext.setVisibility(View.GONE);
                bids.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);
                lay.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
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
                db.child("groups").child(groupID).child("bids").child(bidsID).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ProgressBar mProgress= (ProgressBar) findViewById(R.id.loadingProgressBar);
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterAuction( AuctionActivity.this, helper.retrieve(bidsID));
                        lv.setAdapter(adapter);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();

                bidsExistListener(bidsID);
                db.child("groups").child(groupID).child("bids").child(bidsID).addChildEventListener(new ChildEventListener()
                {
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
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}

        });

        lv.setAdapter(adapter);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                                    System.out.println("------->" + bidsAddUser);

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
                break;

            default:
                Log.d(TAG, "onActivityResult: uknown request code " + requestCode);
        }
    }

}
