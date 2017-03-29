
package com.flatmate.flatmate.Activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.facebook.login.LoginManager;
import com.flatmate.flatmate.Firebase.AddMembers;
import com.flatmate.flatmate.Firebase.GraphUser;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Other.AlarmProgressReceiver;
import com.flatmate.flatmate.Other.AlarmReceiver;
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.MyStatus;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.FontsOverride;
import com.flatmate.flatmate.Other.Pager;
import com.flatmate.flatmate.Other.SetNotification;
import com.flatmate.flatmate.Other.StringForNotifications;
import com.flatmate.flatmate.Other.WorkDoneReceiver;
import com.flatmate.flatmate.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,TabLayout.OnTabSelectedListener{
    TextView testView;
    //This is our tablayout
    private TabLayout tabLayout;
    private LinearLayout newTask;
    //This is our viewPager
    private ViewPager viewPager;

    public String nameR;
    public String durationR;
    public String deadlineR;
    public String timeR;
    public String dateR;
    public String userID;
    public String userName;
    public String userEmail;
    public String uniqueID;
    public String groupID;
    public String actualMonth;
    public String userGroupID;
    public String userGroupIDChildKey;

    public String evaluationBidsID;
    public String evaluationDeadline;
    public String evaluationStatus;
    public String evaluationTime;
    public String evaluationDate;
    public String childKey;
    public Integer finall;
    public Integer notifCounter;

    public DatabaseReference db;
    public FirebaseHelperWork helper;
    private FirebaseAuth firebaseAuth;

    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;

    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean isActivityActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isActivityActual = true;
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_main_activity);
        //FontsOverride.setDefaultFont(this, "DEFAULT", "customfont.ttf");
        
        firebaseAuth = FirebaseAuth.getInstance();
        notifCounter = 1;
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            isActivityActual = false;
            startActivity(intent);
        }


        init();
        setUserName();
        control_notification();
        setEvaluationByDB();

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new Pager(getSupportFragmentManager(),getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

    }

    public void setEvaluationByDB()
    {

        db = FirebaseDatabase.getInstance().getReference();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if ( isActivityActual == true)
                {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    groupID = value.get("_group").toString();
                    if (groupID.length() != 0) {
                        db.child("groups").child(groupID).child("works").child("todo").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s)
                            {
                                if ( isActivityActual == true) {
                                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                    String key = dataSnapshot.getKey().toString();

                                    if (value.get("_bidsID") == null) {
                                        db.child("groups").child(groupID).child("works").child("todo").child(key).setValue(null);
                                    } else {
                                        evaluationBidsID = value.get("_bidsID").toString();
                                        evaluationDeadline = value.get("_deadline").toString();
                                        evaluationStatus = value.get("_status").toString();

                                        MyStatus statusC = new MyStatus();

                                        String statusInString = statusC.setStatus(evaluationStatus, MainActivity.this);
                                        evaluationStatus = statusInString;

                                        evaluationDate = value.get("_date").toString();
                                        evaluationTime = value.get("_time").toString();

                                        Integer Year, Month, Day, Hour, Minute;

                                        if (evaluationStatus.equals(getString(R.string.status_auctioning))) {
                                            Hour = Integer.valueOf(evaluationDeadline.substring(0, 2));
                                            Minute = Integer.valueOf(evaluationDeadline.substring(3, 5));
                                            Day = Integer.valueOf(evaluationDeadline.substring(6, 8));
                                            Month = Integer.valueOf(evaluationDeadline.substring(9, 11));
                                            Year = Integer.valueOf(evaluationDeadline.substring(12, 16));

                                            Calendar cal = Calendar.getInstance();

                                            cal.set(Calendar.YEAR, Year);
                                            cal.set(Calendar.MONTH, Month - 1);
                                            cal.set(Calendar.DAY_OF_MONTH, Day);
                                            cal.set(Calendar.HOUR_OF_DAY, Hour);
                                            cal.set(Calendar.MINUTE, Minute);
                                            cal.set(Calendar.SECOND, 0);
                                            cal.set(Calendar.MILLISECOND, 0);

                                            int _id = (int) System.currentTimeMillis();

                                            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                            intent.putExtra("alarmId", _id);
                                            intent.putExtra("bidsID", evaluationBidsID);
                                            intent.putExtra("groupID", groupID);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                            PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                            cal = null;
                                        }

                                        if (evaluationStatus.equals(getString(R.string.status_done))) {
                                            Hour = Integer.valueOf(evaluationDeadline.substring(0, 2));
                                            Minute = Integer.valueOf(evaluationDeadline.substring(3, 5));
                                            Day = Integer.valueOf(evaluationDeadline.substring(6, 8));
                                            Month = Integer.valueOf(evaluationDeadline.substring(9, 11));
                                            Year = Integer.valueOf(evaluationDeadline.substring(12, 16));

                                            Calendar cal = Calendar.getInstance();

                                            cal.set(Calendar.YEAR, Year);
                                            cal.set(Calendar.MONTH, Month - 1);
                                            cal.set(Calendar.DAY_OF_MONTH, Day);
                                            cal.set(Calendar.HOUR_OF_DAY, Hour);
                                            cal.set(Calendar.MINUTE, Minute);
                                            cal.set(Calendar.SECOND, 0);
                                            cal.set(Calendar.MILLISECOND, 0);

                                            int _id = (int) System.currentTimeMillis();

                                            Intent intent = new Intent(MainActivity.this, WorkDoneReceiver.class);

                                            intent.putExtra("alarmId", _id);
                                            intent.putExtra("bidsID", evaluationBidsID);
                                            intent.putExtra("groupID", groupID);
                                            intent.putExtra("groupID", groupID);
                                            intent.putExtra("childKey", key);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                            PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                            cal = null;
                                        }

                                        if (evaluationStatus.equals(getString(R.string.status_progress))) {
                                            evaluationDeadline = evaluationTime + " " + evaluationDate;
                                            Hour = Integer.valueOf(evaluationDeadline.substring(0, 2));
                                            Minute = Integer.valueOf(evaluationDeadline.substring(3, 5));
                                            Day = Integer.valueOf(evaluationDeadline.substring(6, 8));
                                            Month = Integer.valueOf(evaluationDeadline.substring(9, 11));
                                            Year = Integer.valueOf(evaluationDeadline.substring(12, 16));

                                            Calendar cal = Calendar.getInstance();

                                            cal.set(Calendar.YEAR, Year);
                                            cal.set(Calendar.MONTH, Month - 1);
                                            cal.set(Calendar.DAY_OF_MONTH, Day);
                                            cal.set(Calendar.HOUR_OF_DAY, Hour);
                                            cal.set(Calendar.MINUTE, Minute);
                                            cal.set(Calendar.SECOND, 0);
                                            cal.set(Calendar.MILLISECOND, 0);

                                            int _id = (int) System.currentTimeMillis();

                                            Intent intent = new Intent(MainActivity.this, AlarmProgressReceiver.class);

                                            intent.putExtra("alarmId", _id);
                                            intent.putExtra("bidsID", evaluationBidsID);
                                            intent.putExtra("groupID", groupID);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                            PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
                                            cal = null;
                                        }

                                        if (evaluationStatus.equals(getString(R.string.status_uncompleted))) {
                                            Hour = Integer.valueOf(evaluationDeadline.substring(0, 2));
                                            Minute = Integer.valueOf(evaluationDeadline.substring(3, 5));
                                            Day = Integer.valueOf(evaluationDeadline.substring(6, 8));
                                            Month = Integer.valueOf(evaluationDeadline.substring(9, 11));
                                            Year = Integer.valueOf(evaluationDeadline.substring(12, 16));

                                            Calendar cal = Calendar.getInstance();

                                            cal.set(Calendar.YEAR, Year);
                                            cal.set(Calendar.MONTH, Month - 1);
                                            cal.set(Calendar.DAY_OF_MONTH, Day);
                                            cal.set(Calendar.HOUR_OF_DAY, Hour);
                                            cal.set(Calendar.MINUTE, Minute);
                                            cal.set(Calendar.SECOND, 0);
                                            cal.set(Calendar.MILLISECOND, 0);

                                            int _id = (int) System.currentTimeMillis();

                                            Intent intent = new Intent(MainActivity.this, WorkDoneReceiver.class);

                                            intent.putExtra("alarmId", _id);
                                            intent.putExtra("bidsID", evaluationBidsID);
                                            intent.putExtra("groupID", groupID);
                                            intent.putExtra("groupID", groupID);
                                            intent.putExtra("childKey", key);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                            PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                            alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                            cal = null;
                                        }
                                    }
                                }
                            }
                            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
                            {
                                if ( isActivityActual == true) {
                                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                    String key = dataSnapshot.getKey().toString();
                                    evaluationBidsID = value.get("_bidsID").toString();
                                    evaluationDeadline = value.get("_deadline").toString();
                                    evaluationStatus = value.get("_status").toString();

                                    MyStatus statusC = new MyStatus();

                                    String statusInString = statusC.setStatus(evaluationStatus, MainActivity.this);
                                    evaluationStatus = statusInString;

                                    evaluationDate = value.get("_date").toString();
                                    evaluationTime = value.get("_time").toString();

                                    Integer Year, Month, Day, Hour, Minute;

                                    if (evaluationStatus.equals(getString(R.string.status_progress))) {
                                        evaluationDeadline = evaluationTime + " " + evaluationDate;
                                        Hour = Integer.valueOf(evaluationDeadline.substring(0, 2));
                                        Minute = Integer.valueOf(evaluationDeadline.substring(3, 5));
                                        Day = Integer.valueOf(evaluationDeadline.substring(6, 8));
                                        Month = Integer.valueOf(evaluationDeadline.substring(9, 11));
                                        Year = Integer.valueOf(evaluationDeadline.substring(12, 16));

                                        Calendar cal = Calendar.getInstance();

                                        cal.set(Calendar.YEAR, Year);
                                        cal.set(Calendar.MONTH, Month - 1);
                                        cal.set(Calendar.DAY_OF_MONTH, Day);
                                        cal.set(Calendar.HOUR_OF_DAY, Hour);
                                        cal.set(Calendar.MINUTE, Minute);
                                        cal.set(Calendar.SECOND, 0);
                                        cal.set(Calendar.MILLISECOND, 0);

                                        int _id = (int) System.currentTimeMillis();

                                        Intent intent = new Intent(MainActivity.this, AlarmProgressReceiver.class);

                                        intent.putExtra("alarmId", _id);
                                        intent.putExtra("bidsID", evaluationBidsID);
                                        intent.putExtra("groupID", groupID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                        PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
                                        cal = null;
                                    }

                                    if (evaluationStatus.equals(getString(R.string.status_uncompleted))) {
                                        Hour = Integer.valueOf(evaluationDeadline.substring(0, 2));
                                        Minute = Integer.valueOf(evaluationDeadline.substring(3, 5));
                                        Day = Integer.valueOf(evaluationDeadline.substring(6, 8));
                                        Month = Integer.valueOf(evaluationDeadline.substring(9, 11));
                                        Year = Integer.valueOf(evaluationDeadline.substring(12, 16));

                                        Calendar cal = Calendar.getInstance();

                                        cal.set(Calendar.YEAR, Year);
                                        cal.set(Calendar.MONTH, Month - 1);
                                        cal.set(Calendar.DAY_OF_MONTH, Day);
                                        cal.set(Calendar.HOUR_OF_DAY, Hour);
                                        cal.set(Calendar.MINUTE, Minute);
                                        cal.set(Calendar.SECOND, 0);
                                        cal.set(Calendar.MILLISECOND, 0);

                                        int _id = (int) System.currentTimeMillis();

                                        Intent intent = new Intent(MainActivity.this, WorkDoneReceiver.class);

                                        intent.putExtra("alarmId", _id);
                                        intent.putExtra("bidsID", evaluationBidsID);
                                        intent.putExtra("groupID", groupID);
                                        intent.putExtra("groupID", groupID);
                                        intent.putExtra("childKey", key);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                        PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                        alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                        cal = null;
                                    }
                                }
                            }
                            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void control_notification()
    {
        db = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("notifications").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if ( isActivityActual == true)
                {
                        Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        String notif_group_name = value.get("_group_name").toString();

                        String notif_message = value.get("_message").toString();
                        String notif_message2 = value.get("_message2").toString();

                        String work_date = value.get("_date").toString();
                        String tmp;
                        Integer tmp2;

                        try
                        {
                            tmp = value.get("_random").toString();
                            tmp2 = Integer.valueOf(tmp);
                            System.out.println("------> " + "test");
                        }catch (Exception e)
                        {
                            tmp2 = 12345;
                        }

                        Integer notif_counter = tmp2.intValue();

                        String childKey = dataSnapshot.getKey();
                        Boolean showNotification = getActualDate(work_date);

                        if (!showNotification) {
                            db.child("user").child("users").child(userID).child("notifications").child(childKey).setValue(null);
                        } else {
                            StringForNotifications sfn = new StringForNotifications();
                            String notificaton_message = sfn.setNotificationString(notif_message, notif_message2, MainActivity.this);

                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent, 0);

                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            long[] vibrate = {0, 200};

                            Notification notif = new Notification.Builder(MainActivity.this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setSound(alarmSound)
                                    .setContentTitle(notificaton_message)
                                    .setContentText(notif_group_name)
                                    .setContentIntent(pIntent).build();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                            notif.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

                            notificationManager.notify(notif_counter, notif);

                            db.child("user").child("users").child(userID).child("notifications").child(childKey).setValue(null);
                        }
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void setUserName(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        View hView =  navigationView.getHeaderView(0);

        final TextView userNameView = (TextView) hView.findViewById(R.id.menuUserNameView);
        final TextView userEmailView = (TextView) hView.findViewById(R.id.menuUserEmailView);

        db = FirebaseDatabase.getInstance().getReference();
        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if ( isActivityActual == true) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    userName = value.get("_name").toString();
                    userEmail = value.get("_email").toString();
                    userGroupID = value.get("_group").toString();
                    userGroupIDChildKey = dataSnapshot.getKey();

                    if (userName != null && userEmail != null)
                    {
                        userNameView.setText(userName);
                        userEmailView.setText(userEmail);

                        db.child("user").child("groups").child("user").child(userID).child("user").orderByChild("_group_ID").equalTo(userGroupID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                    String groupName = value.get("_group_name").toString();
                                    final TextView group = (TextView) findViewById(R.id.menuUserGroupView);
                                    group.setText(groupName);

                                }
                            }
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });

                        db.child("user").child("users").child(userID).child("messages").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                final String addGroupID = value.get("_group_ID").toString();
                                final String addGroupName = value.get("_group_name").toString();
                                String senderEmail = value.get("_sender_email").toString();
                                final String key = dataSnapshot.getKey().toString();

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(R.string.Request).setMessage(getString(R.string.user) + senderEmail + getString(R.string.add_you) + addGroupName)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, int id) {

                                                Date actDate = new Date();
                                                Calendar actCal = Calendar.getInstance();
                                                actCal.setTime(actDate);
                                                Integer monthInInt = actCal.get(Calendar.MONTH) + 1;
                                                actualMonth = getMonth(monthInInt);
                                                SetNotification set = new SetNotification();
                                                set.Set(addGroupID, 2, userName, "1", "");
                                                final TextView group = (TextView) findViewById(R.id.menuUserGroupView);
                                                group.setText(addGroupName);

                                                db.child("groups").child(addGroupID).child("graph").child("months").child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                            Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                            String childKey = childSnapshot.getKey();
                                                            String stringMemCount = value.get("_membersCount").toString();

                                                            Integer memCount = Integer.valueOf(stringMemCount);
                                                            memCount = memCount + 1;

                                                            Map newUserData = new HashMap();
                                                            newUserData.put("_membersCount", memCount.toString());
                                                            db.child("groups").child(addGroupID).child("graph").child("months").child("members").child(childKey).updateChildren(newUserData);
                                                        }

                                                        db.child("groups").child(addGroupID).child("graph").child("months").child(actualMonth).child("control").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                                    childKey = childSnapshot.getKey();
                                                                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                                    String memC = value.get("_membersCount").toString();
                                                                    finall = Integer.valueOf(memC);

                                                                    db.child("groups").child(addGroupID).child("graph").child("months").child(actualMonth).child("users").orderByChild("_ID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            if (dataSnapshot.getValue() == null) {
                                                                                GraphUser graphUser = new GraphUser();
                                                                                graphUser.set_ID(userID);
                                                                                graphUser.set_name(userName);
                                                                                graphUser.set_email(userEmail);
                                                                                graphUser.set_credits("0");
                                                                                db.child("groups").child(addGroupID).child("graph").child("months").child(actualMonth).child("users").push().setValue(graphUser);

                                                                                finall++;

                                                                                Map newUserData = new HashMap();
                                                                                newUserData.put("_membersCount", finall.toString());
                                                                                db.child("groups").child(addGroupID).child("graph").child("months").child(actualMonth).child("control").child(childKey).updateChildren(newUserData);

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {
                                                                        }
                                                                    });

                                                                    final NewGroup newGroupUser = new NewGroup();
                                                                    final NewGroup newGroupMembers = new NewGroup();

                                                                    newGroupUser.set_group_ID(addGroupID);
                                                                    newGroupUser.set_user_email(userEmail);
                                                                    newGroupUser.set_group_name(addGroupName);
                                                                    newGroupUser.set_admin("false");
                                                                    db.child("user").child("groups").child("user").child(userID).child("user").push().setValue(newGroupUser);

                                                                    newGroupMembers.set_user_email(userEmail);
                                                                    newGroupMembers.set_user_ID(userID);
                                                                    newGroupMembers.set_user_name(userName);
                                                                    db.child("user").child("groups").child("members").child(addGroupID).child("members").push().setValue(newGroupMembers);

                                                                    db.child("user").child("users").child(userID).child("messages").child(key).setValue(null);

                                                                    if (userGroupID.equals("")) {
                                                                        Map newData = new HashMap();
                                                                        newData.put("_group", addGroupID);
                                                                        db.child("user").child("users").child(userID).child("data").child(userGroupIDChildKey).updateChildren(newData);
                                                                    }

                                                                    dialog.cancel();
                                                                    Toast.makeText(MainActivity.this, getString(R.string.group) + addGroupName + getString(R.string.group_added), Toast.LENGTH_LONG).show();

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                db.child("user").child("users").child(userID).child("messages").child(key).setValue(null);
                                                dialog.cancel();
                                            }
                                        });

                                try
                                {
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                catch (Exception e) {}

                            }

                            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void showZaire(View view)
    {
        db.child("user").child("users").child(userID).child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                    String childKey = childSnapshot.getKey();
                    String existGroupID = value.get("_group").toString();

                    if (!existGroupID.equals(""))
                    {
                        startActivityForResult(new Intent(MainActivity.this, AddNewWorkActivity.class), AddNewWorkActivity.ADD_FINISHED);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                    else
                        Toast.makeText(MainActivity.this, R.string.add_new_work_toast,Toast.LENGTH_LONG).show();

                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {} });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

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

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    public void notifPref(View view)
    {
        Intent intent = new Intent(this, AppPreferences.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        isActivityActual = false;

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.to_do, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings)
        {
            startActivityForResult(new Intent(MainActivity.this, AppPreferences.class), AppPreferences.SETTINGS_FINISHED);
        }

        return super.onOptionsItemSelected(item);
    }

    public void init()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                if (id == R.id.nav_to_do)
                {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isActivityActual = false;
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                }
                else if (id == R.id.nav_activity_graph)
                {
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_settings)
                {
                    //Intent intent = new Intent(MainActivity.this, AppPreferences.class);
                    //startActivity(intent);
                    startActivityForResult(new Intent(MainActivity.this, AppPreferences.class), AppPreferences.SETTINGS_FINISHED);

                }
                else if (id == R.id.nav_my_group)
                {
                    //Intent intent = new Intent(MainActivity.this, MyGroupsActivity.class);
                    //startActivity(intent);
                    startActivityForResult(new Intent(MainActivity.this, MyGroupsActivity.class), MyGroupsActivity.GROUP_FINISHED);
                }
                else if (id == R.id.nav_add_group)
                {
                    Intent intent = new Intent(MainActivity.this, CreateNewGroupActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_log_out)
                {
                    try {
                        LoginManager.getInstance().logOut();
                    }
                    catch (Exception e) {}

                    try
                    {
                        SignInActivity signIn = new SignInActivity();
                        signIn.signOut();
                    }
                    catch (Exception e) {}

                    firebaseAuth.signOut();
                    finish();

                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isActivityActual = false;
                    startActivity(intent);
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public Boolean getActualDate(String work_date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        final long ONE_MINUTE_IN_MILLIS = 60000;

        Date now = new Date();
        Date myDateTime = null;

        String strDate = simpleDateFormat.format(now);
        String myString = work_date;
        try
        {
            myDateTime = simpleDateFormat.parse(myString);
            now = simpleDateFormat.parse(strDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        long curTimeInMs = myDateTime.getTime();

        Date afterAddingMins = new Date(curTimeInMs + ( 1440 * ONE_MINUTE_IN_MILLIS));

        if ( now.compareTo(afterAddingMins) > 0 )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, R.string.google_play_service_error, Toast.LENGTH_SHORT).show();

    }

    public void setAuctionDeadline()
    {
        Date now = new Date();

        String myString = timeR + " " + dateR;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date myDateTime = null;

        String strDate = simpleDateFormat.format(now);

        //Parse your string to SimpleDateFormat
        try
        {
            myDateTime = simpleDateFormat.parse(myString);
            now = simpleDateFormat.parse(strDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        //System.out.println("This is the Actual Date:"+myDateTime);

        Calendar cal = new GregorianCalendar();
        cal.setTime(now);

        if ( deadlineR.equals(getString(R.string.one_hour)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        else if ( deadlineR.equals(getString(R.string.three_hours)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 2);
        }
        else if ( deadlineR.equals(getString(R.string.six_hours)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 6);
        }
        else if ( deadlineR.equals(getString(R.string.twelve_hours)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 12);
        }
        else if ( deadlineR.equals(getString(R.string.twentyfour_hours)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 24);
        }
        else if ( deadlineR.equals(getString(R.string.fourtyeight_hours)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 48);
        }
        else if ( deadlineR.equals(getString(R.string.week)))
        {
            cal.add(Calendar.HOUR_OF_DAY, 168);
        }

        simpleDateFormat.setTimeZone(cal.getTimeZone());
        deadlineR = simpleDateFormat.format(cal.getTime()).toString();
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit)
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public void setUnspecifiedDeadline()
    {
        Date date1;
        Date date2;
        double difference;
        double tmp;
        Date myDateTime = null;

        Calendar cal1 = Calendar.getInstance();
        date1 = cal1.getTime();

        String myString = timeR + " " + dateR;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        try
        {
            myDateTime = simpleDateFormat.parse(myString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar cal = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();

        cal.setTime(myDateTime);
        date2= cal.getTime();

        difference = getDateDiff(date1,date2,TimeUnit.MINUTES);

        if ( difference < 2880)
        {
            tmp = (difference / 100) * 30;
        }
        else
        {
            tmp = (difference / 100) * 50;
        }

        cal2.add(Calendar.MINUTE, (int) tmp);

        simpleDateFormat.setTimeZone(cal2.getTimeZone());

        deadlineR = simpleDateFormat.format(cal2.getTime()).toString();

        System.out.println("-----> " + deadlineR);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AddNewWorkActivity.ADD_FINISHED:
                if ( data == null)
                {
                    return;
                }

                if ( isOnline() == false)
                {
                    Toast.makeText(getBaseContext(), R.string.internet_connection, Toast.LENGTH_SHORT).show();
                }
                else
                {

                    String[] split = data.getStringExtra(AddNewWorkActivity.SELECTED_ADD_KEY).split("-");
                    String str = Arrays.toString(split).replace("[", "").replace("]", "");
                    dataresultTostrings(str);

                    uniqueID = UUID.randomUUID().toString();
                    db = FirebaseDatabase.getInstance().getReference();
                    helper = new FirebaseHelperWork(db);
                    NewWork newWork = new NewWork();
                    newWork.set_work_name(nameR);
                    newWork.set_duration(durationR + " min");

                    if (!deadlineR.equals("null")) {
                        setAuctionDeadline();
                    } else {
                        setUnspecifiedDeadline();
                    }

                    newWork.set_deadline(deadlineR);
                    newWork.set_date(dateR);
                    newWork.set_time(timeR);
                    newWork.set_status("1");
                    newWork.set_bidsID(uniqueID);
                    newWork.set_bidsID(uniqueID);
                    newWork.set_userEmail("null");

                    newWork.set_bidsLastValue("null");
                    newWork.set_bidsAddUsers("null");
                    newWork.set_bidsLastUser("null");
                    newWork.set_bidsLastUserName("null");
                    newWork.set_bidsCount("0");
                    newWork.set_workProgress("0");

                    helper.save(newWork);
                    CustomAdapterToDo adapter = new CustomAdapterToDo(this, helper.retrieve());
                }
                break;

            case MyGroupsActivity.GROUP_FINISHED:
                if ( data == null)
                {
                    return;
                }

                String controlCase = data.getStringExtra("my_groups_activity_result");

                System.out.println("-------> " + controlCase);

                if(controlCase.equals("1"))
                {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isActivityActual = false;
                    finish();
                    startActivity(intent);
                }
                break;

            case AppPreferences.SETTINGS_FINISHED:
                if ( data == null)
                {
                    return;
                }
                String control = data.getStringExtra("control");

                if ( control.equals("1"))
                {
                    String renameUser = data.getStringExtra("rename");
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isActivityActual = false;
                    finish();
                    startActivity(intent);
                }

                if ( control.equals("2"))
                {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    isActivityActual = false;
                    finish();
                    startActivity(intent);
                }

                break;


            default:
                Log.d(TAG, "onActivityResult: uknown request code " + requestCode);
        }
    }

    public void dataresultTostrings(String str)
    {
        int hashCount = 0;
        char[] charArray = str.toCharArray();
        int size = str.length();
        int x = 0;
        int from = 0;
        boolean bool = false;

        while ( x <= size )
        {
            if ( charArray[x] == '#')
            {
                if( hashCount == 0 && bool == false )
                {
                    char[] charArray1 = Arrays.copyOfRange(charArray, from, x);
                    nameR = new String(charArray1);
                    from = x+1;
                    hashCount = 1;
                    bool = true;
                    x++;
                }
                if( hashCount == 1 && bool == false)
                {
                    char[] charArray2 = Arrays.copyOfRange(charArray, from, x);
                    durationR = new String(charArray2);
                    from = x+1;
                    hashCount = 2;
                    bool = true;
                    x++;
                }
                if( hashCount == 2 && bool == false)
                {
                    char[] charArray3 = Arrays.copyOfRange(charArray, from, x);
                    deadlineR = new String(charArray3);
                    from = x+1;
                    hashCount = 3;
                    bool = true;
                    x++;
                }
                if( hashCount == 3 && bool == false)
                {
                    char[] charArray4 = Arrays.copyOfRange(charArray, from, x);
                    dateR = new String(charArray4);
                    from = x+1;
                    hashCount = 4;
                    bool = true;
                    x++;
                }
                if( hashCount == 4 && bool == false)
                {
                    char[] charArray5 = Arrays.copyOfRange(charArray, from, x);
                    timeR = new String(charArray5);
                    from = x+1;
                    break;
                }
            }
            else
            {
                x++;
                bool = false;
            }
        }

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
