package com.flatmate.flatmate.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class GraphActivity extends AppCompatActivity
{
    private static String TAG = "MainActivity";
    public String monthString;
    private Integer[] yData;
    private String[] xData;
    public PieChart pieChart;
    public Integer membersCount;
    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    String userID;
    public Integer tmp_get;
    public Spinner dropdown1;
    public String monthYear;

    public boolean startGraph;
    public PieDataSet pieDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        startGraph = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_layout);
        Log.d(TAG, "onCreate: starting to create chart");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30);

        membersCount = 0;

        tmp_get = Calendar.getInstance().get(Calendar.MONTH)+1;
        if ( tmp_get == 13)
            tmp_get = 1;
        switch (tmp_get) {
            case 1:  monthString = "January";
                break;
            case 2:  monthString = "February";
                break;
            case 3:  monthString = "March";
                break;
            case 4:  monthString = "April";
                break;
            case 5:  monthString = "May";
                break;
            case 6:  monthString = "June";
                break;
            case 7:  monthString = "July";
                break;
            case 8:  monthString = "August";
                break;
            case 9:  monthString = "September";
                break;
            case 10: monthString = "October";
                break;
            case 11: monthString = "November";
                break;
            case 12: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }

        dropdown1 = (Spinner) findViewById(R.id.spinnerGraph);
        String[] items = new String[]
                {
                        "January",
                        "February",
                        "March",
                        "April",
                        "May",
                        "June",
                        "July",
                        "August",
                        "September",
                        "October",
                        "November",
                        "December"
                };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown1.setAdapter(adapter);

        findInDB();
    }

    public void findInDB()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).child("data").addChildEventListener(new ChildEventListener()
        {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                if(groupID.length() != 0)
                {
                    db.child("groups").child(groupID).child("graph").child("months").child(monthString).child("control").addChildEventListener(new ChildEventListener() {
                        @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                            monthYear = value.get("_month").toString();
                            db.child("groups").child(groupID).child("graph").child("months").child("members").addChildEventListener(new ChildEventListener() {
                                @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                    String members = value.get("_membersCount").toString();

                                    /*Members member = new Members();
                                    member.set_membersCount("3");
                                    db.child("groups").child(groupID).child("graph").child("months").child("members").push().setValue(member);*/

                                    setGraph(members);
                                }
                                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                @Override public void onCancelled(DatabaseError databaseError) {}
                            });
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

    public void changeMonthToView(Integer nPos)
    {
        switch (nPos) {
            case 1:  monthString = "January";
                break;
            case 2:  monthString = "February";
                break;
            case 3:  monthString = "March";
                break;
            case 4:  monthString = "April";
                break;
            case 5:  monthString = "May";
                break;
            case 6:  monthString = "June";
                break;
            case 7:  monthString = "July";
                break;
            case 8:  monthString = "August";
                break;
            case 9:  monthString = "September";
                break;
            case 10: monthString = "October";
                break;
            case 11: monthString = "November";
                break;
            case 12: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        membersCount = 0;

        findInDB();
    }

    public void setGraph(String members)
    {
        db = FirebaseDatabase.getInstance().getReference();
        final Integer tmp = Integer.parseInt(members);

        yData = new Integer[tmp];
        xData = new String[tmp];

        if ( !monthYear.equals("null"))
        {
            db.child("groups").child(groupID).child("graph").child("months").child(monthString).child("users").addChildEventListener(new ChildEventListener()
            {
                @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    String name = value.get("_name").toString();
                    String credits = value.get("_credits").toString();

                    yData[membersCount] = Integer.parseInt(credits);
                    xData[membersCount] = name;

                    membersCount++;
                    if (membersCount == tmp) {

                        pieChart = (PieChart) findViewById(R.id.pieChart);
                        pieChart.setRotationEnabled(true);
                        pieChart.setUsePercentValues(true);
                        pieChart.setNoDataTextColor(Color.parseColor("#EF6C00"));
                        pieChart.setCenterTextColor(Color.BLACK);
                        pieChart.setHoleRadius(40f);
                        pieChart.setTransparentCircleAlpha(0);
                        pieChart.setCenterText(monthString + " " + monthYear);
                        pieChart.setCenterTextSize(15);
                        pieChart.setEntryLabelTextSize(12);
                        addDataSet();
                    }
                }

                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override public void onCancelled(DatabaseError databaseError) {}
            });
        }
        else
        {
            pieChart = (PieChart) findViewById(R.id.pieChart);
            pieChart.setRotationEnabled(true);
            pieChart.setNoDataTextColor(Color.parseColor("#EF6C00"));
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.setHoleRadius(40f);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.setCenterText(monthString + " " + "- no data");
            pieChart.setCenterTextSize(15);
            pieChart.setEntryLabelTextSize(12);
            addDataSet();
        }
    }

    public String addName(int i)
    {
        return xData[i];
    }

    private void addDataSet()
    {

        if ( monthYear.equals("null"))
        {
            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();

            membersCount = 0;
            pieDataSet = new PieDataSet(yEntrys, "");

            yEntrys.add(new PieEntry( 1 , "No available data"));
            xEntrys.add("No available data");
            pieChart.setUsePercentValues(true);
        }
        else
        {
            //Log.d(TAG, "addDataSet started");
            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();

            pieDataSet = new PieDataSet(yEntrys, "");

            for(int i = 0; i < yData.length; i++){
                yEntrys.add(new PieEntry(yData[i] , addName(i)));
            }

            for(int i = 1; i < xData.length; i++){
                xEntrys.add(xData[i]);
            }
        }

        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(14);

        if ( monthYear.equals("null"))
        {
            pieDataSet.setColors(Color.parseColor("#2196F3"));
        }
        else
        {
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#EF6C00"));
            colors.add(Color.parseColor("#0091EA"));
            colors.add(Color.parseColor("#689F38"));
            colors.add(Color.parseColor("#E64A19"));
            colors.add(Color.parseColor("#7C4DFF"));
            colors.add(Color.parseColor("#FF80AB"));
            colors.add(Color.parseColor("#689F38"));
            colors.add(Color.parseColor("#FFD740"));
            colors.add(Color.parseColor("#0097A7"));
            colors.add(Color.parseColor("#9E9E9E"));
            colors.add(Color.parseColor("#00796B"));
            pieDataSet.setColors(colors);
        }
        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

        ProgressBar load = (ProgressBar) findViewById(R.id.loadingProgressBarGraph);
        com.github.mikephil.charting.charts.PieChart pieGraph = (com.github.mikephil.charting.charts.PieChart) findViewById(R.id.pieChart);
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerGraph);
        spinner.setVisibility(View.VISIBLE);

        if ( startGraph == true)
        {
            spinner.setSelection(tmp_get-1);
        }

        load.setVisibility(View.GONE);
        pieGraph.setVisibility(View.VISIBLE);

        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                    if( startGraph == false)
                    {
                        int nPos = position;
                        changeMonthToView(nPos+1);
                    }

                    if ( startGraph == true)
                    {
                        startGraph = false;
                    }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }
}
