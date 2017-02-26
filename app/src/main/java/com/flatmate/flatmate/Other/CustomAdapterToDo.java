package com.flatmate.flatmate.Other;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterToDo extends BaseAdapter{

    Context c;
    ArrayList<NewWork> a;

    public CustomAdapterToDo(Context c, ArrayList<NewWork> a) {
        this.c = c;
        this.a = a;
    }
    @Override
    public int getCount() {
        return a.size();
    }
    @Override
    public Object getItem(int position) {
        return a.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.task_to_do_final,parent,false);
        }

        TextView textViewTaskName = (TextView) convertView.findViewById(R.id.textViewTaskName);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatus);
        TextView textViewtime2 = (TextView) convertView.findViewById(R.id.textViewtime2);
        TextView textViewtime = (TextView) convertView.findViewById(R.id.textViewTime);

        final NewWork s= (NewWork) this.getItem(position);
        String pom;
        String work_name = "";

        if(s.get_work_name().length() > 20)
        {
            pom = s.get_work_name();
            char[] charArray = pom.toCharArray();
            for( int i = 0; i < 20; i++)
            {
                work_name = work_name + String.valueOf(charArray[i]);
            }
            work_name = work_name + "...";
        }
        else
            work_name = s.get_work_name();

        if(!s.get_status().equals("Status : auctioning"))
        {
            textViewtime2.setText(s.get_bidsLastUserName());
            textViewtime.setText("Who won?");
            textViewtime.setTextSize(17);
        }
        else
            textViewtime2.setText(s.get_deadline());

        if(s.get_status().equals("Status : done"))
        {
            textViewtime2.setText(s.get_bidsLastUserName());
            textViewtime.setText("Work completed by :");
            textViewtime.setTextSize(14);
            textViewStatus.setTextColor(Color.parseColor("#ff669900"));
        }

        textViewTaskName.setText(work_name);
        textViewStatus.setText(s.get_status());

        return convertView;
    }
}