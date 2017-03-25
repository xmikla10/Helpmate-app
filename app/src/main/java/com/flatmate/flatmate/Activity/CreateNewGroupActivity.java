package com.flatmate.flatmate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.flatmate.flatmate.Firebase.GraphUser;
import com.flatmate.flatmate.Firebase.Members;
import com.flatmate.flatmate.Firebase.Months;
import com.flatmate.flatmate.Firebase.NewBid;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NotificationMessage;
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.SetNotification;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class CreateNewGroupActivity extends AppCompatActivity {

    private ProgressDialog progressDialogCreating;
    String group_name;
    String user_name;
    String user_email;
    String userEmail;
    String group_ID;
    String userID;
    ArrayList<String> memberEmail;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;
    String actualMonth;
    public String userGroupID;
    public String userGroupIDChildKey;
    public String isEmailEnteredAlready;

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
    ArrayList<AutoCompleteTextView> editTexts = new ArrayList<>();
    Integer count;
    Integer memCount;
    Integer isContactLoaded;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setLocale();

        setContentView(R.layout.activity_add_group_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyGroups);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressDialogCreating = new ProgressDialog(this);

        oneClick = 1;
        isContactLoaded = 1;
        count = 0;
        memberEmail = new ArrayList<String>();

        mPeopleList = new ArrayList<Map<String, String>>();
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.mmWhoNo);
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.custcontview,
                new String[] { "Name", "Phone" } , new int[] {
                R.id.ccontName, R.id.ccontNo});
        mTxtPhoneNo.setAdapter(mAdapter);
        AutoCompleteTextView first = (AutoCompleteTextView) findViewById(R.id.mmWhoNo) ;
        editTexts.add(first);

        mLayout = (LinearLayout) findViewById(R.id.addNewMemberLayout);
        mButton = (Button) findViewById(R.id.buttonAdd);
        mButton.setOnClickListener(onClick());
        TextView textView = new TextView(this);
        textView.setText("email");

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



        final FloatingActionButton createGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if ( isOnline() == false)
                {
                    Toast.makeText(getBaseContext(), R.string.internet_connection, Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialogCreating.setMessage(getString(R.string.progress_creating_group));
                    progressDialogCreating.setCancelable(false);
                    progressDialogCreating.show();
                    isEmailEnteredAlready = "";

                    int size = memberEmail.size();
                    String wrongEmail = "";
                    cnt = 0;
                    db = FirebaseDatabase.getInstance().getReference();
                    Boolean goodEmails = true;

                    firebaseAuth = FirebaseAuth.getInstance();
                    userEmail = firebaseAuth.getCurrentUser().getEmail().toString();
                    EditText groupName = (EditText) findViewById(R.id.editTextGroupName);
                    group_name = groupName.getText().toString();
                    group_ID = UUID.randomUUID().toString();

                    for (EditText editText : editTexts) {
                        if (!editText.getText().toString().equals("")) {
                            if (!isValidEmail(editText.getText())) {
                                wrongEmail = editText.getText().toString();
                                goodEmails = false;
                                break;
                            }
                        }
                    }

                    if (goodEmails) {
                        final int itemCount = editTexts.size();
                        memCount = 0;

                        for (EditText editText : editTexts) {
                            final String email = editText.getText().toString();

                            db.child("user").child("groups").child("find").orderByChild("_user_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    memCount++;
                                    if (dataSnapshot.getValue() == null) {
                                        //generovat email
                                        System.out.println("" + "");
                                    }

                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                        String ID = value.get("_user_ID").toString();

                                        if (isEmailEnteredAlready.indexOf(email) != -1 || userEmail.equals(email)) {
                                            System.out.println("------->" + "Zhoda - email bol už zadaný");
                                        } else {
                                            isEmailEnteredAlready = isEmailEnteredAlready + email + " ,";
                                            AddMembers addMembers = new AddMembers();
                                            addMembers.set_group_ID(group_ID);
                                            addMembers.set_sender_email(userEmail);
                                            addMembers.set_group_name(group_name);
                                            db.child("user").child("users").child(ID).child("messages").push().setValue(addMembers);

                                            NotificationMessage notif = new NotificationMessage();
                                            notif.set_message("9");
                                            notif.set_message2(group_name);
                                            notif.set_group_name(group_name);
                                            notif.set_work_ID("9");
                                            notif.set_date(getActualDate());
                                            notif.set_random(generateRandomNumber().toString());
                                            db.child("user").child("users").child(ID).child("notifications").push().setValue(notif);
                                        }
                                    }
                                    if (memCount == itemCount) {
                                        createGroup();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    } else {
                        progressDialogCreating.dismiss();
                        Toast.makeText(CreateNewGroupActivity.this, wrongEmail + getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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
                userGroupID = value.get("_group").toString();
                userGroupIDChildKey = dataSnapshot.getKey();

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

                Date actDate= new Date();
                Calendar actCal = Calendar.getInstance();
                actCal.setTime(actDate);
                Integer monthInInt = actCal.get(Calendar.MONTH) + 1;
                actualMonth = getMonth(monthInInt);

                GraphUser graphUser = new GraphUser();
                graphUser.set_ID(userID);
                graphUser.set_name(user_name);
                graphUser.set_email(userEmail);
                graphUser.set_credits("0");
                db.child("groups").child(group_ID).child("graph").child("months").child(actualMonth).child("users").push().setValue(graphUser);

                db.child("groups").child(group_ID).child("graph").child("months").child(actualMonth).child("control").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                        {
                            String childKey = childSnapshot.getKey();
                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();

                            Date actDate= new Date();
                            Calendar actCal = Calendar.getInstance();
                            actCal.setTime(actDate);
                            Integer year = actCal.get(Calendar.YEAR);

                            Map newData = new HashMap();
                            newData.put("_month", year);
                            newData.put("_membersCount", "1");
                            db.child("groups").child(group_ID).child("graph").child("months").child(actualMonth).child("control").child(childKey).updateChildren(newData);
                        }

                        if(userGroupID.equals(""))
                        {
                            Map newData = new HashMap();
                            newData.put("_group", group_ID);
                            db.child("user").child("users").child(userID).child("data").child(userGroupIDChildKey).updateChildren(newData);
                        }

                        progressDialogCreating.dismiss();
                        //Intent intent = new Intent(CreateNewGroupActivity.this, MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        //startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public String getMonth(Integer month)
    {
        String monthString;
        switch (month)
        {
            case 1:  monthString = "January";break;
            case 2:  monthString = "February";break;
            case 3:  monthString = "March";break;
            case 4:  monthString = "April";break;
            case 5:  monthString = "May";break;
            case 6:  monthString = "June";break;
            case 7:  monthString = "July";break;
            case 8:  monthString = "August";break;
            case 9:  monthString = "September";break;
            case 10: monthString = "October";break;
            case 11: monthString = "November";break;
            case 12: monthString = "December";break;
            default: monthString = "Invalid month";break;
        }

        return monthString;
    }

    public final static boolean isValidEmail(CharSequence target)
    {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void openprogresdialog(final View view) {
        // TODO Auto-generated method stub
        final ProgressDialog progDailog = ProgressDialog.show(CreateNewGroupActivity.this, getString(R.string.loading_contacts), getString(R.string.please_wait), true);

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
        ContentResolver cr = getContentResolver();
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

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else
        {
            System.out.println("---------> " + "Nieje pripojenie na internet");
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case AppPreferences.SETTINGS_FINISHED:
                if (data == null) {
                    return;
                }
                String control = data.getStringExtra("control");

                if (control.equals("1")) {
                    String renameUser = data.getStringExtra("rename");
                    Intent intent = new Intent(CreateNewGroupActivity.this, CreateNewGroupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }

                if (control.equals("2")) {
                    Intent intent = new Intent(CreateNewGroupActivity.this, CreateNewGroupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }

                break;


            default:
                Log.d("test", "onActivityResult: uknown request code " + requestCode);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private void setLocale()
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("language", null);

        if (restoredText != null)
        {
            Locale myLocale = new Locale(restoredText);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
        else
        {
            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

    }
}