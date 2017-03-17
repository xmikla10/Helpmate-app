package com.flatmate.flatmate.Other;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.flatmate.flatmate.Activity.MainActivity;
import com.flatmate.flatmate.Firebase.NewBid;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends WakefulBroadcastReceiver
{

    private String groupID;
    String bidsLastValue;
    String bidsLastUser;
    String bidsCount;
    String bidsAddUser;
    String bidsLastUserName;
    String childKey;
    String work_name;
    DatabaseReference db;
    String  bidsID;
    private FirebaseAuth firebaseAuth;
    String userID;
    String status;

    String date;
    String time;


    @Override
    public void onReceive(final Context context, Intent intent)
    {
        int alarmId = intent.getExtras().getInt("alarmId");
        bidsID = intent.getExtras().getString("bidsID");
        groupID = intent.getExtras().getString("groupID");

        db = FirebaseDatabase.getInstance().getReference();
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
                    bidsLastUser = value.get("_bidsLastUser").toString();
                    bidsLastUserName = value.get("_bidsLastUserName").toString();
                    work_name = value.get("_work_name").toString();
                    status = value.get("_status").toString();
                    date = value.get("_date").toString();
                    time = value.get("_time").toString();
                }

                if ( status.equals("Status : auctioning"))
                {
                    Map newEvaluationData = new HashMap();

                    if ( bidsLastUser.equals("null"))
                    {
                        newEvaluationData.put("_status", "Status : unauctioned");

                        db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newEvaluationData);
                        SetNotification set = new SetNotification();
                        set.Set(groupID, 8, work_name, bidsID);
                    }
                    else
                    {
                        newEvaluationData.put("_userEmail", bidsLastUser);
                        newEvaluationData.put("_status", "Status : in progress");

                        db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newEvaluationData);
                        SetNotification set = new SetNotification();
                        set.Set(groupID, 6, work_name, bidsID);
                        set.Set(groupID, 4, work_name, bidsID);

                    }
                }

            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        /*Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        setResultCode(Activity.RESULT_OK);*/

    }
}
