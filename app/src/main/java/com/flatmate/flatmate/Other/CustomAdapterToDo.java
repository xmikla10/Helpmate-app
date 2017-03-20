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

import com.flatmate.flatmate.Activity.MainActivity;
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

        MyStatus statusC = new MyStatus();

        String statusInString = statusC.setStatus( s.get_status(), c);


        if(statusInString.equals(c.getString(R.string.status_auctioning)))
        {
            textViewtime.setText(R.string.duration_of_auction);
            textViewStatus.setTextColor(Color.BLACK);
            textViewTaskName.setTextColor(Color.BLACK);
            textViewtime2.setText(s.get_deadline());
        }
        else if(statusInString.equals(c.getString(R.string.status_progress)))
        {
            textViewtime2.setText(s.get_bidsLastUserName());
            textViewTaskName.setTextColor(Color.BLACK);
            textViewtime.setText(R.string.who_won_1);
            textViewStatus.setTextColor(Color.BLACK);
            textViewtime.setTextSize(17);
        }
        else if(statusInString.equals(c.getString(R.string.status_done)))
        {
            textViewtime2.setText(s.get_bidsLastUserName());
            textViewTaskName.setTextColor(Color.BLACK);
            textViewtime.setText(R.string.work_completed_by_1);
            textViewtime.setTextSize(14);
            textViewStatus.setTextColor(Color.parseColor("#ff669900"));
        }
        else if(statusInString.equals(c.getString(R.string.status_unauctioned)))
        {
            textViewtime2.setText("");
            textViewtime.setText(R.string.delete_ask);
            textViewStatus.setTextColor(Color.RED);
            textViewTaskName.setTextColor(Color.RED);
        }
        else if(statusInString.equals(c.getString(R.string.status_uncompleted)))
        {
            textViewtime2.setText(s.get_bidsLastUserName());
            textViewtime.setText(R.string.wrk_uncompleted_by);
            textViewStatus.setTextColor(Color.RED);
            textViewTaskName.setTextColor(Color.RED);
        }
        else
            textViewtime2.setText(s.get_deadline());

        textViewTaskName.setText(work_name);
        textViewStatus.setText(statusInString);

        return convertView;
    }
}