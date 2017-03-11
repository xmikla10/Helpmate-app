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

    /**
     * control : - 1 = AddNewWork
     *           - 2 =
     */

    public void Set(String groupID, final Integer control)
    {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("groups").child("members").child(groupID).child("members").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    String childKey = childSnapshot.getKey();
                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                    String find_user = value.get("_user_ID").toString();

                    if (  control == 1)
                    {
                        if( !find_user.equals(userID))
                        {
                            NotificationMessage notif_1 = new NotificationMessage();
                            String message_1 = "The new work added";
                            notif_1.set_message(message_1);
                            db.child("user").child("users").child(find_user).child("data").setValue(message_1);
                        }
                    }
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}});
    }
}
