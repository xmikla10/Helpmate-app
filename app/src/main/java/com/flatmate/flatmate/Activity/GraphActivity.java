package com.flatmate.flatmate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.Members;
import com.flatmate.flatmate.Firebase.NewWork;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private Integer[] yData;
    private String[] xData;
    PieChart pieChart;

    public Integer membersCount;

    private FirebaseAuth firebaseAuth;
    private String groupID;
    DatabaseReference db;
    NewWork newWork1;
    String userID;
    String uniqueID;
    Boolean saved=null;
    ArrayList<NewWork> a =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_layout);
        Log.d(TAG, "onCreate: starting to create chart");

        membersCount = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid().toString();

        db.child("user").child("users").child(userID).addChildEventListener(new ChildEventListener()
        {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                groupID = value.get("_group").toString();
                    if(groupID.length() != 0)
                    {
                        db.child("groups").child(groupID).child("graph").child("months").child("February").child("members").addChildEventListener(new ChildEventListener() {
                            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                String members = value.get("_membersCount").toString();
                                setGraph(members);
                            }
                            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            @Override public void onCancelled(DatabaseError databaseError) {}
                        });
                        /*Members members = new Members();
                        members.set_membersCount("3");
                        db.child("groups").child(groupID).child("graph").child("months").child("February").child("members").push().setValue(members);*/
                    }

            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void setGraph(String members)
    {
        db = FirebaseDatabase.getInstance().getReference();
        final Integer tmp = Integer.parseInt(members);

        yData = new Integer[tmp];
        xData = new String[tmp];

        db.child("groups").child(groupID).child("graph").child("months").child("February").child("users").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String name = value.get("_name").toString();
                String credits = value.get("_credits").toString();

                yData[membersCount]= Integer.parseInt(credits);
                xData[membersCount]= name;

                System.out.println(yData[membersCount]);
                System.out.println(xData[membersCount]);

                membersCount++;
                if (membersCount == tmp)
                {
                    pieChart = (PieChart) findViewById(R.id.pieChart);
                    pieChart.setRotationEnabled(true);
                    //pieChart.setUsePercentValues(true);
                    pieChart.setNoDataTextColor(Color.parseColor("#EF6C00"));
                    pieChart.setCenterTextColor(Color.BLACK);
                    pieChart.setHoleRadius(40f);
                    pieChart.setTransparentCircleAlpha(0);
                    pieChart.setCenterText("January");
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

    public String addName(int i)
    {
        System.out.println("tusom");
        return xData[i];
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");

        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] , addName(i)));
        }

        for(int i = 1; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }

        //create the data set
        //List<PieEntry> entries = new ArrayList<>();

       /* entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));
        entries.add(new PieEntry(44.32f, "Blue"));*/

        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(14);

        //add colors to dataset
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
        load.setVisibility(View.GONE);
        pieGraph.setVisibility(View.VISIBLE);
    }
}
