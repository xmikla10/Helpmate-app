package com.flatmate.flatmate.Other;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flatmate.flatmate.Activity.AuctionActivity;
import com.flatmate.flatmate.Firebase.FirebaseHelperAuction;
import com.flatmate.flatmate.Firebase.NewBid;
import com.flatmate.flatmate.R;
import com.flatmate.flatmate.lib.ExceptionHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Peter on 11/4/2016.
 */

public class BidPopUp extends Activity
{

    public static final String SELETECTED_BID_KEY = "bid_result";
    public static final int BID_POPUP_FINISHED = 125;
    NumberPicker np;

    private FirebaseAuth firebaseAuth;
    private String groupID;
    String userID;
    String childKey;
    String bidsAddUser;
    DatabaseReference db;
    String bidsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set exception handler that prints info to log
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        setContentView(R.layout.popup_bid);

        Bundle extras = getIntent().getExtras();

        if( extras != null)
        {
            bidsID = extras.getString("bidsID");
        }

        DisplayMetrics dm = new DisplayMetrics();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Display getOrient = getWindowManager().getDefaultDisplay();

        int screenWidth = (int) (metrics.widthPixels * 0.90);
        int screenWidth2 = (int) (metrics.widthPixels * 0.60);

        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int rotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getOrientation();

        if( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
        {
            getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else
            getWindow().setLayout(screenWidth2, ViewGroup.LayoutParams.WRAP_CONTENT);


        Button btnbid = (Button) findViewById(R.id.button_bid);
        final CheckBox checkboxbid = (CheckBox) findViewById(R.id.checkBoxBid);

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(false);

        db = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(bidsID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            childKey = childSnapshot.getKey();
                            Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                            bidsAddUser = value.get("_bidsAddUsers").toString();
                        }

                        if (bidsAddUser.indexOf(userID) != -1)
                        {
                            checkboxbid.setVisibility(View.GONE);
                        }
                        else
                            checkboxbid.setVisibility(View.VISIBLE);

                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}

        });

        btnbid.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                int np_count = np.getValue();
                String bid = String.valueOf(np_count);
                if ( checkboxbid.isChecked() )
                {
                    bid = "not interested";
                }
                Intent intent = new Intent();
                intent.putExtra(SELETECTED_BID_KEY, bid);
                setResult(BID_POPUP_FINISHED,intent);
                finish();
            }
        });

    }

}
