
package com.flatmate.flatmate.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.flatmate.flatmate.Other.AlarmReceiver;
import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.Pager;
import com.flatmate.flatmate.Other.WorkDoneReceiver;
import com.flatmate.flatmate.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.flatmate.flatmate.Firebase.FirebaseHelperWork;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
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

    public String evaluationBidsID;
    public String evaluationDeadline;
    public String evaluationStatus;



    public DatabaseReference db;
    public FirebaseHelperWork helper;
    private FirebaseAuth firebaseAuth;

    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LogInActivity.class));
        }

        init();
        setUserName();
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

        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                if(groupID.length() != 0)
                {
                    db.child("groups").child(groupID).child("works").child("todo").addChildEventListener(new ChildEventListener() {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                String key = dataSnapshot.getKey().toString();
                                System.out.println("--------> " + key);
                                evaluationBidsID = value.get("_bidsID").toString();
                                evaluationDeadline = value.get("_deadline").toString();
                                evaluationStatus = value.get("_status").toString();

                                Integer Year, Month, Day, Hour, Minute;

                                if ( evaluationStatus.equals("Status : auctioning"))
                                {
                                    Hour = Integer.valueOf(evaluationDeadline.substring(0,2));
                                    Minute = Integer.valueOf(evaluationDeadline.substring(3,5));
                                    Day = Integer.valueOf(evaluationDeadline.substring(6,8));
                                    Month = Integer.valueOf(evaluationDeadline.substring(9,11));
                                    Year = Integer.valueOf(evaluationDeadline.substring(12,16));

                                    Calendar cal = Calendar.getInstance();

                                    cal.set(Calendar.YEAR, Year);
                                    cal.set(Calendar.MONTH, Month-1);
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

                                    PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                    cal = null;
                                }

                                if ( evaluationStatus.equals("Status : done"))
                                {
                                    Hour = Integer.valueOf(evaluationDeadline.substring(0,2));
                                    Minute = Integer.valueOf(evaluationDeadline.substring(3,5));
                                    Day = Integer.valueOf(evaluationDeadline.substring(6,8));
                                    Month = Integer.valueOf(evaluationDeadline.substring(9,11));
                                    Year = Integer.valueOf(evaluationDeadline.substring(12,16));

                                    Calendar cal = Calendar.getInstance();

                                    cal.set(Calendar.YEAR, Year);
                                    cal.set(Calendar.MONTH, Month-1);
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

                                    PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager1.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                                    cal = null;
                                }
                        }
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
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
        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                userName = value.get("_name").toString();
                userEmail = value.get("_email").toString();
                if(userName != null && userEmail != null)
                {
                    userNameView.setText(userName);
                    userEmailView.setText(userEmail);
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }
    public void showAuction(View view)
    {
        Intent intent = new Intent(this, AuctionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    public void showZaire(View view) {
        Intent intent = new Intent(this, AddNewWorkActivity.class);
        startActivityForResult(new Intent(this, AddNewWorkActivity.class), AddNewWorkActivity.ADD_FINISHED);
        //startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void notifPref(View view)
    {
        Intent intent = new Intent(this, AppPreferences.class);
        startActivity(intent);
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
            Intent intent6 = new Intent(this, AppPreferences.class);
            startActivity(intent6);
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
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
                if (id == R.id.nav_to_do) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else if (id == R.id.nav_activity_graph) {
                    startActivity(new Intent(MainActivity.this, GraphActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, AppPreferences.class));
                } else if (id == R.id.nav_my_group) {
                    startActivity(new Intent(MainActivity.this, MyGroupsActivity.class));
                } else if (id == R.id.nav_join_group) {
                    startActivity(new Intent(MainActivity.this, JoinGroupActivity.class));
                } else if (id == R.id.nav_add_group) {
                    startActivity(new Intent(MainActivity.this, CreateNewGroupActivity.class));
                } else if (id == R.id.nav_log_out) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(MainActivity.this, LogInActivity.class));
                    overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void setAuctionDeadline()
    {

        String myString = timeR + " " + dateR;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date myDateTime = null;

        //Parse your string to SimpleDateFormat
        try
        {
            myDateTime = simpleDateFormat.parse(myString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        //System.out.println("This is the Actual Date:"+myDateTime);

        Calendar cal = new GregorianCalendar();
        cal.setTime(myDateTime);

        if ( deadlineR.equals("1 hour"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        else if ( deadlineR.equals("3 hours"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 2);
        }
        else if ( deadlineR.equals("6 hours"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 6);
        }
        else if ( deadlineR.equals("12 hours"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 12);
        }
        else if ( deadlineR.equals("24 hours"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 24);
        }
        else if ( deadlineR.equals("48 hours"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 48);
        }
        else if ( deadlineR.equals("week"))
        {
            cal.add(Calendar.HOUR_OF_DAY, 168);
        }

        //System.out.println("This is Hours Added Date:"+cal.getTime());

        deadlineR = cal.getTime().toString();
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
        long difference;
        long tmp;
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
            tmp = (difference / 100)* 20;
        }
        else
        {
            tmp = (difference / 100)* 50;
        }

        difference = (difference - tmp)/60;

        cal2.add(Calendar.HOUR_OF_DAY, (int) difference);

        simpleDateFormat.setTimeZone(cal2.getTimeZone());

        deadlineR = simpleDateFormat.format(cal2.getTime()).toString();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AddNewWorkActivity.ADD_FINISHED:
                if ( data == null)
                    return;
                String[] split = data.getStringExtra(AddNewWorkActivity.SELECTED_ADD_KEY).split("-");
                String str = Arrays.toString(split).replace("[", "").replace("]", "");
                dataresultTostrings(str);

                uniqueID = UUID.randomUUID().toString();
                db = FirebaseDatabase.getInstance().getReference();
                helper = new FirebaseHelperWork(db);
                NewWork newWork = new NewWork();
                newWork.set_work_name(nameR);
                newWork.set_duration(durationR + " min");

                if ( !deadlineR.equals("null"))
                {
                    setAuctionDeadline();
                }
                else
                {
                    setUnspecifiedDeadline();
                }

                newWork.set_deadline(deadlineR);
                newWork.set_date(dateR);
                newWork.set_time(timeR);
                newWork.set_status("Status : auctioning");
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
}
