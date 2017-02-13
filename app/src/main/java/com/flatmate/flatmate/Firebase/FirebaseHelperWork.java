package com.flatmate.flatmate.Firebase;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class FirebaseHelperWork
{
    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    NewWork newWork1;
    String userID;
    String uniqueID;
    Boolean saved=null;
    ArrayList<NewWork> a =new ArrayList<>();

    public FirebaseHelperWork(DatabaseReference db) {
        this.db = db;
    }
    //WRITE
    public Boolean save(final NewWork newWork)
    {
        if(newWork == null)
        {
            saved=false;
        }else
        {
            try
            {
                newWork1 = newWork;
                firebaseAuth = FirebaseAuth.getInstance();
                uniqueID = UUID.randomUUID().toString();
                userID = firebaseAuth.getCurrentUser().getUid().toString();
                db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
                     @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                         Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                         groupID = value.get("_group").toString();
                         if(groupID.length() != 0)
                         {
                             db.child("groups").child(groupID).child("works").child("todo").push().setValue(newWork1);

                             /*GraphUser graphUser1 = new GraphUser();
                             graphUser1.set_name("Peter");
                             graphUser1.set_email("a@a.com");
                             graphUser1.set_ID("f51MP4By4Dfu7ltoxfhtkvNSfk63");
                             graphUser1.set_credits("5");

                             GraphUser graphUser2 = new GraphUser();
                             graphUser2.set_name("Thomas");
                             graphUser2.set_email("b@a.com");
                             graphUser2.set_ID("3zQM6sZYi3O8Yfq6enhIVbAkd3h1");
                             graphUser2.set_credits("10");

                             GraphUser graphUser3 = new GraphUser();
                             graphUser3.set_name("Miley");
                             graphUser3.set_email("c@a.com");
                             graphUser3.set_ID("wgCR977V8vOj1d4hGtHgHpH7IXA2");
                             graphUser3.set_credits("15");

                             db.child("groups").child(groupID).child("graph").child("months").child("February").child("users").push().setValue(graphUser1);
                             db.child("groups").child(groupID).child("graph").child("months").child("February").child("users").push().setValue(graphUser2);
                             db.child("groups").child(groupID).child("graph").child("months").child("February").child("users").push().setValue(graphUser3);*/
                         }
                     }
                     @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                     @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                     @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                     @Override public void onCancelled(DatabaseError databaseError) {}
                 });

                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }
    //READ
    public ArrayList<NewWork> retrieve()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {fetchData(dataSnapshot);}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        return a;
    }
    private void fetchData(DataSnapshot dataSnapshot)
    {
        a.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            NewWork newWork = ds.getValue(NewWork.class);
            a.add(newWork);
        }
    }

}