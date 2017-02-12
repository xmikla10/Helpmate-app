
package com.flatmate.flatmate.Activity;

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

import com.flatmate.flatmate.Other.AppPreferences;
import com.flatmate.flatmate.Other.CustomAdapterToDo;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.Other.Pager;
import com.flatmate.flatmate.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

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

    public DatabaseReference db;
    public FirebaseHelperWork helper;
    private FirebaseAuth firebaseAuth;

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
                } else if (id == R.id.nav_account) {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, AppPreferences.class));
                } else if (id == R.id.nav_my_group) {
                    startActivity(new Intent(MainActivity.this, MyGroupsActivity.class));
                } else if (id == R.id.nav_join_group) {
                    startActivity(new Intent(MainActivity.this, JoinGroupActivity.class));
                } else if (id == R.id.nav_add_group) {
                    startActivity(new Intent(MainActivity.this, CreateNewGroupActivity.class));
                } else if (id == R.id.nav_about_flatmate) {
                    startActivity(new Intent(MainActivity.this, AboutFlatmateActivity.class));
                    overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
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
                newWork.set_deadline(deadlineR);
                newWork.set_date(dateR);
                newWork.set_time(timeR);
                newWork.set_status("Status : auctioning");
                newWork.set_bidsID(uniqueID);
                newWork.set_bidsID(uniqueID);

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
