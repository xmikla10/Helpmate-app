package com.flatmate.flatmate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.AddMembers;
import com.flatmate.flatmate.Firebase.Members;
import com.flatmate.flatmate.Firebase.Months;
import com.flatmate.flatmate.Firebase.NewBid;
import com.flatmate.flatmate.Firebase.NewGroup;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class CreateNewGroupActivity extends AppCompatActivity {

    String group_name;
    String user_name;
    String user_email;
    String userEmail;
    String group_ID;
    String userID;
    ArrayList<String> memberEmail;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    private LinearLayout mLayout;
    private AutoCompleteTextView mEditText;
    private Button mButton;

    ListView lvItem;
    private Button btnAdd;
    String displayName="", emailAddress="", phoneNumber="";
    private ArrayList<Map<String, String>> mPeopleList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;
    Integer oneClick;
    Integer cnt;
    private ProgressDialog progressDialog;
    ArrayList<AutoCompleteTextView> editTexts = new ArrayList<>();
    Integer count;
    Integer memCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_layout);
        oneClick = 1;
        count = 0;
        memberEmail = new ArrayList<String>();

        mPeopleList = new ArrayList<Map<String, String>>();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview,
                new String[] { "Name", "Phone" } , new int[] {
                R.id.ccontName, R.id.ccontNo});
        System.out.println(mPeopleList);
        mTxtPhoneNo.setAdapter(mAdapter);
        AutoCompleteTextView first = (AutoCompleteTextView) findViewById(R.id.mmWhoNo) ;
        editTexts.add(first);

        mLayout = (LinearLayout) findViewById(R.id.addNewMemberLayout);
        mButton = (Button) findViewById(R.id.buttonAdd);
        mButton.setOnClickListener(onClick());
        TextView textView = new TextView(this);
        textView.setText("email");
        progressDialog = new ProgressDialog(CreateNewGroupActivity.this);

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
                if (hasFocus)
                {
                    openprogresdialog(v);
                }
            }
        });



        final FloatingActionButton createGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);

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
                EditText groupName = (EditText) findViewById(R.id.editTextGroupName);
                group_name = groupName.getText().toString();
                group_ID = UUID.randomUUID().toString();

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
                    System.out.println("----->" + itemCount);


                    for (EditText editText : editTexts)
                    {
                        final String email = editText.getText().toString();
                        System.out.println("----->" + editTexts);

                            db.child("user").child("groups").child("find").orderByChild("_user_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    System.out.println("--------->" + email);
                                    memCount++;
                                    if (dataSnapshot.getValue() == null)
                                    {
                                        System.out.println("--------->" + "nenasiel som");
                                    }

                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                                    {
                                        String childKey = childSnapshot.getKey();
                                        Map<String,Object> value = (Map<String, Object>) childSnapshot.getValue();
                                        String ID = value.get("_user_ID").toString();
                                        System.out.println("---------> " + ID);

                                        AddMembers addMembers = new AddMembers();
                                        addMembers.set_group_ID(group_ID);
                                        addMembers.set_sender_email(userEmail);
                                        addMembers.set_group_name(group_name);
                                        db.child("user").child("users").child(ID).child("messages").push().setValue(addMembers);
                                    }
                                    if ( memCount == itemCount)
                                    {
                                        System.out.println("----->" + itemCount);
                                        System.out.println("----->" + memCount);
                                        createGroup();
                                    }
                                }
                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });
                    }
                }
                else
                    Toast.makeText(CreateNewGroupActivity.this, wrongEmail +" - wrong email address", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void createGroup()
    {
        final NewGroup newGroupUser = new NewGroup();
        final NewGroup newGroupMembers = new NewGroup();
        final NewGroup newGroupFind = new NewGroup();

        final Months months1 = new Months();
        final Months months2 = new Months();
        final Months months3 = new Months();
        final Months months4 = new Months();
        final Months months5 = new Months();
        final Months months6 = new Months();
        final Months months7 = new Months();
        final Months months8 = new Months();
        final Months months9 = new Months();
        final Months months10 = new Months();
        final Months months11 = new Months();
        final Months months12 = new Months();
        final Members mem = new Members();

        months1.set_month("null"); months1.set_membersCount("null");
        months2.set_month("null"); months2.set_membersCount("null");
        months3.set_month("null"); months3.set_membersCount("null");
        months4.set_month("null"); months4.set_membersCount("null");
        months5.set_month("null"); months5.set_membersCount("null");
        months6.set_month("null"); months6.set_membersCount("null");
        months7.set_month("null"); months7.set_membersCount("null");
        months8.set_month("null"); months8.set_membersCount("null");
        months9.set_month("null"); months9.set_membersCount("null");
        months10.set_month("null"); months10.set_membersCount("null");
        months11.set_month("null"); months11.set_membersCount("null");
        months12.set_month("null"); months12.set_membersCount("null");
        mem.set_membersCount("1");

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                user_email= value.get("_email").toString();
                user_name = value.get("_name").toString();

                newGroupUser.set_group_ID(group_ID);
                newGroupUser.set_user_email(user_email);
                newGroupUser.set_group_name(group_name);
                newGroupUser.set_admin("true");
                db.child("user").child("groups").child("user").child(userID).child("user").push().setValue(newGroupUser);

                newGroupMembers.set_user_email(user_email);
                newGroupMembers.set_user_ID(userID);
                newGroupMembers.set_user_name(user_name);
                db.child("user").child("groups").child("members").child(group_ID).child("members").push().setValue(newGroupMembers);

                db.child("groups").child(group_ID).child("graph").child("months").child("January").child("control").push().setValue(months1);
                db.child("groups").child(group_ID).child("graph").child("months").child("February").child("control").push().setValue(months2);
                db.child("groups").child(group_ID).child("graph").child("months").child("March").child("control").push().setValue(months3);
                db.child("groups").child(group_ID).child("graph").child("months").child("April").child("control").push().setValue(months4);
                db.child("groups").child(group_ID).child("graph").child("months").child("May").child("control").push().setValue(months5);
                db.child("groups").child(group_ID).child("graph").child("months").child("June").child("control").push().setValue(months6);
                db.child("groups").child(group_ID).child("graph").child("months").child("July").child("control").push().setValue(months7);
                db.child("groups").child(group_ID).child("graph").child("months").child("August").child("control").push().setValue(months8);
                db.child("groups").child(group_ID).child("graph").child("months").child("September").child("control").push().setValue(months9);
                db.child("groups").child(group_ID).child("graph").child("months").child("October").child("control").push().setValue(months10);
                db.child("groups").child(group_ID).child("graph").child("months").child("November").child("control").push().setValue(months11);
                db.child("groups").child(group_ID).child("graph").child("months").child("December").child("control").push().setValue(months12);
                db.child("groups").child(group_ID).child("graph").child("months").child("members").push().setValue(mem);

                Intent intent = new Intent(CreateNewGroupActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public final static boolean isValidEmail(CharSequence target)
    {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void openprogresdialog(final View view) {
        // TODO Auto-generated method stub
        final ProgressDialog progDailog = ProgressDialog.show(CreateNewGroupActivity.this, "Loading contacts", "Please wait...", true);

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
                mLayout.addView(createNewTextView("name@email.com"));
            }
        };
    }

    private EditText createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final AutoCompleteTextView editText = new AutoCompleteTextView(this);

        editTexts.add( editText);
        editText.setLayoutParams(lparams);
        editText.setTop(25);
        editText.setHint("name@email.com");
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
        startManagingCursor(cursor);
    }

}