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
import android.widget.Toast;

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

import com.flatmate.flatmate.R;

/**
 * Created by xmikla10 on 29.10.2016.
 */

public class GraphActivity extends AppCompatActivity
{

    private static String TAG = "MainActivity";

    private float[] yData = {25.3f, 35.3f, 18.3f, 28.3f, 26.3f};
    private String[] xData = {"Mitch", "Jessica" , "Mohammad" , "Kelsey", "Sam"};
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_layout);
        Log.d(TAG, "onCreate: starting to create chart");

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
        //More options just check out the documentation!

        addDataSet();

    }

    public String addName(int i)
    {
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
    }
}
