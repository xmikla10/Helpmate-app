package com.flatmate.flatmate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.AddMembers;
import com.flatmate.flatmate.Firebase.FirebaseHelperInfoGroup;
import com.flatmate.flatmate.Firebase.FirebaseHelperWorkCompleted;
import com.flatmate.flatmate.Firebase.GraphUser;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.CustomAdapterCompleted;
import com.flatmate.flatmate.Other.CustomAdapterInfoGroup;
import com.flatmate.flatmate.Other.SetNotification;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class GroupInfoActivity extends AppCompatActivity
{
    String groupName;
    String groupID;
    int positionHelp;

    String delete_userName;
    String detele_userID;
    String userEmail;
    String group_ID;
    String group_name;
    Integer memCount;
    Integer isContactLoaded;

    private FirebaseAuth firebaseAuth;
    String userID;
    DatabaseReference db;
    FirebaseHelperInfoGroup helper;
    CustomAdapterInfoGroup adapter;
    NewWork newWork;
    ListView lv;
    ArrayList<NewWork> a =new ArrayList<>();

    String displayName="", emailAddress="", phoneNumber="";
    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;
    Integer oneClick;
    Integer cnt;
    private ProgressDialog progressDialog;
    ArrayList<AutoCompleteTextView> editTexts = new ArrayList<>();
    ArrayList<String> memberEmail;
    private LinearLayout mLayout;
    private AutoCompleteTextView mEditText;
    private Button mButton;
    String admin;
    ImageView renameGroup;
    public String actualGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyGroups);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        isContactLoaded = 1;
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();
        userEmail = firebaseAuth.getCurrentUser().getEmail().toString();


        groupName = extras.getString("group_name");
        groupID = extras.getString("group_ID");
        admin = extras.getString("admin");

        if (!admin.equals("true"))
        {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_info_group);
            TextView textView = (TextView) findViewById(R.id.addMembersTextView);
            AutoCompleteTextView autocom = (AutoCompleteTextView) findViewById(R.id.InfoGroupAutoCompleteText);
            Button addBut = (Button) findViewById(R.id.buttonInfoGroupAdd);

            fab.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            autocom.setVisibility(View.GONE);
            addBut.setVisibility(View.GONE);
        }
        else
        {
            renameGroup = (ImageView) findViewById(R.id.renameGroup);
            renameGroup.setVisibility(View.VISIBLE);
            renameGroup.setOnClickListener(renameGroupListener);
        }

        memberEmail = new ArrayList<String>();
        TextView groupNameText = (TextView) findViewById(R.id.textViewInfoGroupName);
        groupNameText.setText(groupName);

        AutoCompleteTextView first = (AutoCompleteTextView) findViewById(R.id.InfoGroupAutoCompleteText) ;
        editTexts.add(first);

        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperInfoGroup(db);

        adapter = new CustomAdapterInfoGroup(GroupInfoActivity.this, helper.retrieve(groupID), userEmail, admin);
        lv = (ListView) findViewById(R.id.listViewInfoGroup);

        db.child("user").child("groups").child("members").child(groupID).child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                adapter = new CustomAdapterInfoGroup(GroupInfoActivity.this, helper.retrieve(groupID), userEmail, admin);
                lv.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter = new CustomAdapterInfoGroup(GroupInfoActivity.this, helper.retrieve(groupID), userEmail, admin);
                lv.setAdapter(adapter);
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                adapter = new CustomAdapterInfoGroup(GroupInfoActivity.this, helper.retrieve(groupID), userEmail, admin);
                lv.setAdapter(adapter);
            }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });


        // todo - oštetriť aby ak sa vymaže admin, tak aby sa vymazala celá skupina ... aj samotné vymazanie skupiny by tam malo byť
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                positionHelp = position;
                final NewGroup s= (NewGroup) adapter.getItem(position);

                if ( userID.equals(s.get_user_ID()) || admin.equals("true"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                    builder.setMessage(getString(R.string.want_you_delete_user) + s.get_user_name() +" ?")
                            .setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    if( admin.equals("true") || userID.equals(s.get_user_ID()))
                                    {
                                        SetNotification set = new SetNotification();
                                        set.Set(groupID, 3, s.get_user_name(), "1", "");

                                        db.child("user").child("groups").child("members").child(groupID).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                    String childKey = childSnapshot.getKey();
                                                    String delUserID = value.get("_user_ID").toString();

                                                    if (delUserID.equals(s.get_user_ID())) {
                                                        db.child("user").child("groups").child("members").child(groupID).child("members").child(childKey).setValue(null);
                                                    }
                                                }

                                                db.child("user").child("groups").child("user").child(s.get_user_ID()).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                            String childKey = childSnapshot.getKey();
                                                            String delGroupID = value.get("_group_ID").toString();

                                                            if (delGroupID.equals(groupID)) {
                                                                db.child("user").child("groups").child("user").child(s.get_user_ID()).child("user").child(childKey).setValue(null);
                                                            }
                                                        }

                                                        db.child("user").child("users").child(s.get_user_ID()).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                                    String childKey = childSnapshot.getKey();
                                                                    String delGroupID = value.get("_group").toString();

                                                                    if (delGroupID.equals(groupID)) {
                                                                        Map newUserData = new HashMap();
                                                                        newUserData.put("_group", "");
                                                                        db.child("user").child("users").child(s.get_user_ID()).child("data").child(childKey).updateChildren(newUserData);
                                                                    }
                                                                }

                                                                db.child("groups").child(groupID).child("graph").child("months").child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                                            String childKey = childSnapshot.getKey();
                                                                            String stringMemCount = value.get("_membersCount").toString();

                                                                            Integer memCount = Integer.valueOf(stringMemCount);
                                                                            memCount = memCount - 1;

                                                                            Map newUserData = new HashMap();
                                                                            newUserData.put("_membersCount", memCount.toString());
                                                                            db.child("groups").child(groupID).child("graph").child("months").child("members").child(childKey).updateChildren(newUserData);

                                                                            if ( memCount == 0)
                                                                            {
                                                                                db.child("groups").child(groupID).setValue(null);
                                                                                db.child("user").child("groups").child("members").child(groupID).setValue(null);
                                                                            }

                                                                            Intent intent = new Intent(GroupInfoActivity.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                                                        }
                                                                    }
                                                                    @Override public void onCancelled(DatabaseError databaseError) {}
                                                                });}
                                                            @Override public void onCancelled(DatabaseError databaseError) {}
                                                        });}
                                                    @Override public void onCancelled(DatabaseError databaseError) {}
                                                });}
                                            @Override public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                    else
                                        Toast.makeText(GroupInfoActivity.this, R.string.toast_delete, Toast.LENGTH_SHORT).show();

                                }
                            }).setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    //nothing
                                }
                            });
                        AlertDialog alert = builder.create();
                        alert.show();
                }
            }
        });

        mPeopleList = new ArrayList<Map<String, String>>();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.InfoGroupAutoCompleteText);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview,
                new String[] { "Name", "Phone" } , new int[] {
                R.id.ccontName, R.id.ccontNo});
        mTxtPhoneNo.setAdapter(mAdapter);

        mLayout = (LinearLayout) findViewById(R.id.addInfoGroupNewMemberLayout);
        mButton = (Button) findViewById(R.id.buttonInfoGroupAdd);
        mButton.setOnClickListener(onClick());
        TextView textView = new TextView(this);
        textView.setText("email");
        progressDialog = new ProgressDialog(GroupInfoActivity.this);

        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3)
            {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
                String name  = map.get("Name");
                String number = map.get("Phone");
                mTxtPhoneNo.setText(number);
                memberEmail.add(number);
            }
        });

        mTxtPhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus && isContactLoaded == 1)
                {
                    isContactLoaded++;
                    openprogresdialog(v);
                }
            }
        });



        final FloatingActionButton createGroup = (FloatingActionButton) findViewById(R.id.fab_info_group);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int size = memberEmail.size();
                String wrongEmail = "";
                cnt = 0 ;
                db = FirebaseDatabase.getInstance().getReference();
                Boolean goodEmails = true;

                firebaseAuth = FirebaseAuth.getInstance();
                userEmail = firebaseAuth.getCurrentUser().getEmail().toString();
                TextView groupName = (TextView) findViewById(R.id.textViewInfoGroupName);
                group_name = groupName.getText().toString();

                for (EditText editText : editTexts)
                {
                    if (!editText.getText().toString().equals(""))
                    {
                        if (!isValidEmail(editText.getText()))
                        {
                            wrongEmail = editText.getText().toString();
                            goodEmails = false;
                            break;
                        }
                    }
                }

                if ( goodEmails)
                {
                    final int itemCount = editTexts.size();
                    memCount = 0;

                    for (final EditText editText : editTexts)
                    {
                        final String email = editText.getText().toString();

                            db.child("user").child("groups").child("find").orderByChild("_user_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    memCount++;
                                    if (dataSnapshot.getValue() == null)
                                    {
                                        //generovat email
                                        System.out.println("" + "");
                                    }

                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                    {
                                        Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                                        final String ID = value.get("_user_ID").toString();

                                        db.child("user").child("groups").child("members").child(groupID).child("members").orderByChild("_user_ID").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.getValue() == null)
                                                {
                                                    AddMembers addMembers = new AddMembers();
                                                    addMembers.set_group_ID(groupID);
                                                    addMembers.set_sender_email(userEmail);
                                                    addMembers.set_group_name(group_name);
                                                    db.child("user").child("users").child(ID).child("messages").push().setValue(addMembers);
                                                }
                                            }
                                            @Override public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                    if ( memCount == itemCount)
                                    {
                                        if ( memCount == 1 && isContactLoaded != 1)
                                            Toast.makeText(GroupInfoActivity.this, R.string.changes_saved, Toast.LENGTH_SHORT).show();
                                        else
                                        {
                                            if ( isContactLoaded != 1)
                                                Toast.makeText(GroupInfoActivity.this, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
                                        }

                                        Intent intent = new Intent(GroupInfoActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                                    }
                                }
                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });
                    }
                }
                else
                    Toast.makeText(GroupInfoActivity.this, wrongEmail + getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();

            }
        });

    }
    public final static boolean isValidEmail(CharSequence target)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void openprogresdialog(final View view) {
        // TODO Auto-generated method stub
        final ProgressDialog progDailog = ProgressDialog.show(GroupInfoActivity.this, getString(R.string.loading_contacts), getString(R.string.please_wait), true);

        new Thread() {
            public void run() {
                try
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    readContacts();

                }
                catch (Exception e) {
                }
                progDailog.dismiss();
            }
        }.start();
    }

    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.addView(createNewTextView(getString(R.string.name_email)));
            }
        };
    }

    private View.OnClickListener renameGroupListener = new View.OnClickListener() {
        public void onClick(View v)
        {
            final AlertDialog.Builder alert = new AlertDialog.Builder(GroupInfoActivity.this);

            alert.setTitle(R.string.rename_group_title);
            final EditText input = new EditText(GroupInfoActivity.this);
            alert.setView(input);

            alert.setPositiveButton(R.string.rename_group, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    actualGroupName = value;

                    if (value.equals("")) {
                        Toast.makeText(GroupInfoActivity.this, R.string.group_name_empty_toast, Toast.LENGTH_SHORT).show();
                    }
                    else if ( groupName.equals(value))
                    {
                        Toast.makeText(GroupInfoActivity.this, R.string.gropu_rename_toast, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        db.child("user").child("groups").child("members").child(groupID).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                {
                                    Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                                    final String renameGroupUserID = value.get("_user_ID").toString();

                                    db.child("user").child("groups").child("user").child(renameGroupUserID).child("user").orderByChild("_group_ID").equalTo(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                            {
                                                String childKey = childSnapshot.getKey();
                                                Map newGroupName = new HashMap();
                                                newGroupName.put("_group_name", actualGroupName);

                                                db.child("user").child("groups").child("user").child(renameGroupUserID).child("user").child(childKey).updateChildren(newGroupName);
                                            }
                                        }
                                        @Override public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            }
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                        Toast.makeText(GroupInfoActivity.this, R.string.gropu_rename_toast, Toast.LENGTH_SHORT).show();
                        SetNotification set = new SetNotification();
                        set.Set(groupID, 7, groupName, "2", "");

                        TextView groupNameText = (TextView) findViewById(R.id.textViewInfoGroupName);
                        groupNameText.setText(actualGroupName);
                        groupName = actualGroupName;
                    }
                }
            });

            alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton)
                {

                }
            });

            alert.show();

        }
    };

    private EditText createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final AutoCompleteTextView editText = new AutoCompleteTextView(this);

        editTexts.add( editText);
        editText.setLayoutParams(lparams);
        editText.setTop(25);
        editText.setHint(getString(R.string.name_email));
        editText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fb7b0a")));
        editText.setHintTextColor(Color.parseColor("#d1d1d1"));
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview, new String[] { "Name", "Phone" } , new int[] {R.id.ccontName, R.id.ccontNo});
        editText.setAdapter(mAdapter);

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3)
            {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);
                String name  = map.get("Name");
                String number = map.get("Phone");
                editText.setText(number);
                memberEmail.add(number);
            }
        });

        return editText;
    }

    private void readContacts()
    {
        mPeopleList.clear();
        ContentResolver cr =getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext())
        {
            displayName="";emailAddress=""; phoneNumber="";
            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
            while (emails.moveToNext())
            {
                emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                Map<String, String> NamePhoneType = new HashMap<String, String>();
                NamePhoneType.put("Name", displayName);
                NamePhoneType.put("Phone", emailAddress);
                mPeopleList.add(NamePhoneType);
                break;
            }
            emails.close();
        }
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (id == R.id.nav_settings)
        {
            Intent intent6 = new Intent(this, AppPreferences.class);
            startActivity(intent6);
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.to_do, menu);
        return true;
    }

}
