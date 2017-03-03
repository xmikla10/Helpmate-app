package com.flatmate.flatmate.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.FirebaseHelperFindGroup;
import com.flatmate.flatmate.Firebase.FirebaseHelperMyGroups;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Other.CustomAdapterFindGroup;
import com.flatmate.flatmate.Other.CustomAdapterMyGroups;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class JoinGroupActivity extends AppCompatActivity {

    String childKey;
    String userName;
    String userID;
    String tmp;
    String userEmail;
    FirebaseHelperFindGroup helper;
    CustomAdapterFindGroup adapter;
    CustomAdapterFindGroup emptyAdapter;
    ListView lv;
    FirebaseAuth firebaseAuth;
    String user_email;
    String user_ID;
    DatabaseReference db;
    String email;
    ArrayList<NewGroup> a =new ArrayList<>();

    public NewGroup newGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_layout);

        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperFindGroup(db);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();
        db = FirebaseDatabase.getInstance().getReference();
        lv = (ListView) findViewById(R.id.listViewFindGroup);
        userID = firebaseAuth.getCurrentUser().getUid().toString();


        /*final Button findButton = (Button) findViewById(R.id.find_button);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                EditText email = (EditText) findViewById(R.id.editTextFindEmail);
                tmp = email.getText().toString();
                adapter = new CustomAdapterFindGroup( JoinGroupActivity.this, helper.retrieve(tmp));
                lv.setAdapter(adapter);
            }
        });
*/
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newGroup= (NewGroup) adapter.getItem(position);
                userID = firebaseAuth.getCurrentUser().getUid().toString();

                db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        userName = value.get("_name").toString();

                        final NewGroup newGroupUser = new NewGroup();
                        final NewGroup newGroupMembers = new NewGroup();
                        final NewGroup newGroupFind = new NewGroup();

                        newGroupUser.set_group_ID(newGroup.get_group_ID());
                        newGroupUser.set_user_email(userEmail);
                        newGroupUser.set_group_name(newGroup.get_group_name());
                        db.child("user").child("groups").child("user").child(userID).child("user").push().setValue(newGroupUser);

                        newGroupMembers.set_user_email(userEmail);
                        newGroupMembers.set_user_ID(userID);
                        newGroupMembers.set_user_name(userName);
                        db.child("user").child("groups").child("members").child(newGroup.get_group_ID()).push().setValue(newGroupMembers);

                        finish();

                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
    }

    public void find(View view)
    {
        EditText email = (EditText) findViewById(R.id.editTextFindEmail);
        tmp = email.getText().toString();

        db.child("user").child("groups").child("find").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                user_email = value.get("_user_email").toString();
                user_ID = value.get("_user_ID").toString();
                if ( user_email.equals(tmp))
                {
                    db.child("user").child("groups").child("user").child(user_ID).addChildEventListener(new ChildEventListener() {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            TextView notFound = (TextView) findViewById(R.id.textNotFound);
                            notFound.setVisibility(View.GONE);
                            fetchData(dataSnapshot);
                            adapter = new CustomAdapterFindGroup( JoinGroupActivity.this, a);
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            TextView notFound = (TextView) findViewById(R.id.textNotFound);
                            notFound.setVisibility(View.GONE);
                            fetchData(dataSnapshot);
                            adapter = new CustomAdapterFindGroup( JoinGroupActivity.this, a);
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                else
                {
                    TextView notFound = (TextView) findViewById(R.id.textNotFound);
                    notFound.setVisibility(View.VISIBLE);
                    lv.setAdapter(emptyAdapter);
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                user_email = value.get("_user_email").toString();
                user_ID = value.get("_user_ID").toString();

                if ( user_email.equals(tmp))
                {
                    db.child("user").child("groups").child("user").addChildEventListener(new ChildEventListener() {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            TextView notFound = (TextView) findViewById(R.id.textNotFound);
                            notFound.setVisibility(View.GONE);
                            fetchData(dataSnapshot);
                            adapter = new CustomAdapterFindGroup( JoinGroupActivity.this, a);
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            TextView notFound = (TextView) findViewById(R.id.textNotFound);
                            notFound.setVisibility(View.GONE);
                            fetchData(dataSnapshot);
                            adapter = new CustomAdapterFindGroup( JoinGroupActivity.this, a);
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                else
                {
                    TextView notFound = (TextView) findViewById(R.id.textNotFound);
                    notFound.setVisibility(View.VISIBLE);
                    lv.setAdapter(emptyAdapter);
                }
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void fetchData(DataSnapshot dataSnapshot)
    {
        a.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            NewGroup newGroup = ds.getValue(NewGroup.class);
            a.add(newGroup);
        }
    }
}