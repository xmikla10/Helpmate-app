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

    /**
     * control : - 1 = AddNewWork
     *           - 2 = New user added
     *           - 3 = User removed
     *           - 4 = Work win
     *           - 5 = Work done
     *           - 6 = Work change status
     *           - 7 = Work win user
     *           - 8 = Work change status for every members
     */

    public SetNotification() {}

    public void Set(final String groupID, final Integer control, final String string)
    {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

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
                                                    String message_2 = "New user added - " + string;
                                                    notif_2.set_message(message_2);
                                                    notif_2.set_group_name(group_name);
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

                                        if (control == 1) {
                                            if (!find_user.equals(userID)) {
                                                NotificationMessage notif_1 = new NotificationMessage();
                                                String message_1 = "New work added - " + string;
                                                notif_1.set_message(message_1);
                                                notif_1.set_group_name(group_name);
                                                db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_1);
                                            }
                                        }

                                        if (control == 3) {
                                            if (!find_user.equals(userID)) {
                                                NotificationMessage notif_3 = new NotificationMessage();
                                                String message_3 = "User " + string + " removed from group";
                                                notif_3.set_message(message_3);
                                                notif_3.set_group_name(group_name);
                                                db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_3);
                                            }
                                        }

                                        if (control == 4) {
                                            if (find_user.equals(userID)) {
                                                NotificationMessage notif_4 = new NotificationMessage();
                                                String message_4 = "You win auction for: " + string;
                                                notif_4.set_message(message_4);
                                                notif_4.set_group_name(group_name);
                                                db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_4);
                                            }
                                        }

                                        if (control == 5) {
                                            if (!find_user.equals(userID)) {
                                                NotificationMessage notif_5 = new NotificationMessage();
                                                String message_3 = "Work " + string + " done";
                                                notif_5.set_message(message_3);
                                                notif_5.set_group_name(group_name);
                                                db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_5);
                                            }
                                        }

                                        if (control == 6) {
                                            if (!find_user.equals(userID)) {
                                                NotificationMessage notif_6 = new NotificationMessage();
                                                String message_6 = "Work " + string + " change status";
                                                notif_6.set_message(message_6);
                                                notif_6.set_group_name(group_name);
                                                db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_6);
                                            }
                                        }

                                        if (control == 8) {
                                            NotificationMessage notif_8 = new NotificationMessage();
                                            String message_8 = "Work " + string + " change status";
                                            notif_8.set_message(message_8);
                                            notif_8.set_group_name(group_name);
                                            db.child("user").child("users").child(find_user).child("notifications").push().setValue(notif_8);
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
}
