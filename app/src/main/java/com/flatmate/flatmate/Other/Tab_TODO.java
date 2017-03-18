package com.flatmate.flatmate.Other;

/**
 * Created by xmikla10 on 16.11.2016.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flatmate.flatmate.Activity.AddNewWorkActivity;
import com.flatmate.flatmate.Activity.AuctionActivity;
import com.flatmate.flatmate.Activity.MainActivity;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.flatmate.flatmate.Firebase.FirebaseHelperWork;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab_TODO extends Fragment
{
    private ListView listView1;
    private FirebaseAuth firebaseAuth;
    private String groupID;
    public String nameR;
    public String timeR;
    public String dateR;
    public String deadlineR;
    public String uniqueID;
    public String durationR;
    String statusInString;

    String userID;
    DatabaseReference db;
    FirebaseHelperWork helper;
    CustomAdapterToDo adapter;
    NewWork newWork;
    ListView lv;
    ArrayList<NewWork> a =new ArrayList<>();
    private static final String TAG = TabLayout.Tab.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.activity_to_do, container, false);

        newWork = new NewWork();
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelperWork(db);

        adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
        lv = (ListView) rootView.findViewById(R.id.listView1);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                if(groupID.length() == 0)
                {
                    ProgressBar mProgress= (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                    mProgress.setVisibility(View.GONE);
                }



                db.child("groups").child(groupID).child("works").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null)
                        {
                            ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                            mProgress.setVisibility(View.GONE);
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.VISIBLE);
                            adapter = null;
                            lv.setAdapter(adapter);
                        }
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {} });

                    db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener() {
                        ProgressBar mProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s)
                        {
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                            adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                            lv.setAdapter(adapter);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s)
                        {
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                            adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot)
                        {
                            LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                            lay.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                            adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                            lv.setAdapter(adapter);
                        }
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();

                db.child("groups").child(groupID).child("works").addChildEventListener(new ChildEventListener()
                {
                    ProgressBar mProgress= (ProgressBar) rootView.findViewById(R.id.loadingProgressBar);
                    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                        lay.setVisibility(View.GONE);
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        LinearLayout lay = (LinearLayout) rootView.findViewById(R.id.addNewWorkText);
                        lay.setVisibility(View.GONE);
                        mProgress.setVisibility(View.GONE);
                        adapter = new CustomAdapterToDo(getContext(), helper.retrieve());
                        lv.setAdapter(adapter);
                    }
                    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NewWork s= (NewWork) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), AuctionActivity.class);
                final Intent intentRe = new Intent(getActivity(), AddNewWorkActivity.class);

                intent.putExtra("work_name", s.get_work_name());
                intent.putExtra("status", s.get_status());

                MyStatus statusC = new MyStatus();
                statusInString = statusC.setStatus( s.get_status(), getContext());

                intent.putExtra("duration", s.get_duration());
                intent.putExtra("deadline", s.get_deadline());
                intent.putExtra("time", ( s.get_time() +"  "+s.get_date()));
                intent.putExtra("bidsID", s.get_bidsID());

                intent.putExtra("bidsLastValue", s.get_bidsLastValue());
                intent.putExtra("bidsLastUser", s.get_bidsLastUser());
                intent.putExtra("bidsCount", s.get_bidsCount());
                intent.putExtra("workProgress", s.get_workProgress());

                intent.putExtra("myWork", "0");

                if(statusInString.equals(getContext().getString(R.string.status_unauctioned)) || statusInString.equals(getContext().getString(R.string.status_uncompleted)))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Want you repeat this work ?")
                            .setPositiveButton("Repeat", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id)
                                {
                                    intentRe.putExtra("work_name", s.get_work_name());
                                    startActivityForResult( intentRe, AddNewWorkActivity.ADD_FINISHED);
                                    dialog.cancel();
                                    db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(s.get_bidsID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                            {
                                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                String childKey = childSnapshot.getKey();
                                                if ( statusInString.equals(getContext().getString(R.string.status_unauctioned)) || statusInString.equals(getContext().getString(R.string.status_uncompleted)) )
                                                    db.child("groups").child(groupID).child("works").child("todo").child(childKey).setValue(null);
                                            }
                                        }
                                        @Override public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int id)
                                {
                                    db.child("groups").child(groupID).child("works").child("todo").orderByChild("_bidsID").equalTo(s.get_bidsID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                            {
                                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                String childKey = childSnapshot.getKey();
                                                if ( statusInString.equals(getContext().getString(R.string.status_unauctioned)) || statusInString.equals(getContext().getString(R.string.status_uncompleted)) )
                                                    db.child("groups").child(groupID).child("works").child("todo").child(childKey).setValue(null);
                                            }
                                            dialog.cancel();
                                            /*for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                                            {
                                                Map<String, Object> value = (Map<String, Object>) childSnapshot.getValue();
                                                String childKey = childSnapshot.getKey();
                                                db.child("groups").child(groupID).child("works").child("todo").child(childKey).setValue(null);
                                            }
                                            intentRe.putExtra("work_name", s.get_work_name());
                                            startActivity(intentRe);*/

                                        }

                                        @Override public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    if(!statusInString.equals(getContext().getString(R.string.status_done)))
                    {
                        startActivity(intent);
                    }
                }
            }

        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AddNewWorkActivity.ADD_FINISHED:
                if ( data == null)
                {
                    return;
                }

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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                break;

            default:
                Log.d(TAG, "onActivityResult: uknown request code " + requestCode);
        }
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

        difference = getDateDiff(date1,date2, TimeUnit.MINUTES);

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

        //System.out.println("This is Hours Added Date:"+cal.getTime());

        deadlineR = cal.getTime().toString();
    }

}