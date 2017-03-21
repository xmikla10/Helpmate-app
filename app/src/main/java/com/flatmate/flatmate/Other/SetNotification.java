package com.flatmate.flatmate.Other;

import android.util.Log;

import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.flatmate.flatmate.Firebase.NotificationMessage;
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

/**
 * Created by enterprise on 11.3.17.
 */

public class SetNotification
{

    public DatabaseReference db;
    private FirebaseAuth firebaseAuth;
    public String userID;
    public String group_name;
    public String userFromGroup;
    public String work_ID;

    /**
     * control : - 1 = AddNewWork
     *           - 2 = New user added
     *           - 3 = User removed
     *           - 4 = Work win
     *           - 5 = Work done
     *           - 6 = Work change status
     *           - 8 = Work change status for every members
     */

    public SetNotification() {}

    public void Set(final String groupID, final Integer control, final String string, final String workID, final String evaluationEmail)
    {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        work_ID = workID;

        if ( control == 2 )
        {
            db.child("user").child("groups").child("members").child(groupID).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {
                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                        userFromGroup = value.get("_user_ID").toString();
                        break;
                    }
                    db.child("user").child("groups").child("user").child(userFromGroup).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                            {
                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                String group_ID = value.get("_group_ID").toString();
                                String groupN = value.get("_group_name").toString();

                                if (group_ID.equals(groupID))
                                {
                                    group_name = groupN;
                                    db.child("user").child("groups").child("members").child(groupID).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                            {
                                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                String find_user = value.get("_user_ID").toString();

                                                if( !find_user.equals(userID))
                                                {
                                                    NotificationMessage notif_2 = new NotificationMessage();
                                                    String message_2 = "2";
                                                    String message_plus = string;

                                                    notif_2.set_message(message_2);
                                                    notif_2.set_message2(message_plus);
                                                    notif_2.set_group_name(group_name);
                                                    notif_2.set_work_ID(work_ID);
                                                    notif_2.set_date(getActualDate());
                                                    notif_2.set_random(generateRandomNumber().toString());
                                                    db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_2);
                                                }
                                            }
                                        }
                                        @Override public void onCancelled(DatabaseError databaseError) {}});
                                }
                            }
                        }
                        @Override public void onCancelled(DatabaseError databaseError) {}});
                }
                @Override public void onCancelled(DatabaseError databaseError) {}});
        }
        else
        {
            db.child("user").child("groups").child("user").child(userID).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                        String group_ID = value.get("_group_ID").toString();
                        String groupN = value.get("_group_name").toString();

                        if (group_ID.equals(groupID)) {
                            group_name = groupN;
                            db.child("user").child("groups").child("members").child(groupID).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                        String find_user = value.get("_user_ID").toString();
                                        String find_email = value.get("_user_email").toString();

                                        if (control == 1) {
                                            if (!find_user.equals(userID))
                                            {
                                                String message_1 = "1";
                                                String message_plus = string;
                                                sendNotification(message_1, group_name, find_user, message_plus);
                                            }
                                        }

                                        if (control == 3) {
                                            if (!find_user.equals(userID)) {
                                                NotificationMessage notif_3 = new NotificationMessage();
                                                String message_3 = "3";
                                                String message_plus = string;

                                                notif_3.set_message(message_3);
                                                notif_3.set_message2(message_plus);
                                                notif_3.set_group_name(group_name);
                                                notif_3.set_work_ID(work_ID);
                                                notif_3.set_date(getActualDate());
                                                notif_3.set_random(generateRandomNumber().toString());
                                                db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_3);
                                            }
                                        }

                                        if (control == 4) {
                                            if (find_email.equals(evaluationEmail)) {
                                                String message_4 = "4";
                                                String message_plus = string;
                                                sendNotification(message_4, group_name, find_user, message_plus);
                                            }
                                        }

                                        if (control == 5) {
                                            if (!find_user.equals(userID)) {
                                                String message_5 = "5";
                                                String message_plus = string;
                                                sendNotification(message_5, group_name, find_user, message_plus);
                                            }
                                        }

                                        if (control == 6) {
                                            if (!find_email.equals(evaluationEmail)) {
                                                String message_6 = "6";
                                                String message_plus = string;
                                                sendNotification(message_6, group_name, find_user, message_plus);
                                            }
                                        }

                                        if (control == 7) {
                                            if (!find_user.equals(userID)) {
                                                String message_7 = "7";
                                                String message_plus = string;
                                                sendNotification(message_7, group_name, find_user, message_plus);
                                            }
                                        }

                                        if (control == 8)
                                        {
                                            String message_8 = "8";
                                            String message_plus = string;
                                            sendNotification(message_8, group_name, find_user, message_plus);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    public void sendNotification(final String message, final String groupName, final String find_user, final String message2)
    {

        db.child("user").child("users").child(find_user).child("notifications").orderByChild("_work_ID").equalTo(work_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot)
            {

                if ( dataSnapshot.getValue() == null)
                {
                    NotificationMessage notif = new NotificationMessage();
                    notif.set_message(message);
                    notif.set_message2(message2);
                    notif.set_group_name(groupName);
                    notif.set_work_ID(work_ID);
                    notif.set_date(getActualDate());
                    notif.set_random(generateRandomNumber().toString());
                    db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif);
                }
                else
                {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {
                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                        String childKey = childSnapshot.getKey();

                        Map newStatus = new HashMap();
                        newStatus.put("_message", message);
                        newStatus.put("_message2", message2);
                        newStatus.put("_group_name", groupName);
                        newStatus.put("_date", getActualDate());
                        db.child("user").child("users").child(find_user).child("notifications").child(childKey).updateChildren(newStatus);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}});
    }

    public String getActualDate()
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

        String date = dateFormat.format(cal.getTime());

        return date;
    }

    public Double generateRandomNumber()
    {
        double random = Math.random() * 543 + 1;
        double random1 = Math.random() * 223 + 1;
        double random2 = Math.random() * 967 + 1;

        return random + random1 + random2;
    }

}
