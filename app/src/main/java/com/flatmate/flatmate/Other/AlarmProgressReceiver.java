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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlarmProgressReceiver extends WakefulBroadcastReceiver
{

    private String groupID;
    String bidsLastValue;
    String bidsLastUser;
    String bidsCount;
    String bidsAddUser;
    String bidsLastUserName;
    String childKey;
    DatabaseReference db;
    String  bidsID;
    private FirebaseAuth firebaseAuth;
    String userID;
    String status;


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
                    status = value.get("_status").toString();
                }

                if ( status.equals("Status : in progress"))
                {

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

                    Map newEvaluationData = new HashMap();
                    newEvaluationData.put("_status", "Status : uncompleted");
                    newEvaluationData.put("_deadline", dateFormat.format(cal.getTime()));
                    db.child("groups").child(groupID).child("works").child("todo").child(childKey).updateChildren(newEvaluationData);

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
