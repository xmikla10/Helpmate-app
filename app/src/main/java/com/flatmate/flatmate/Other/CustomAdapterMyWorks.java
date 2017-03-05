package com.flatmate.flatmate.Other;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterMyWorks extends BaseAdapter{

    Context c;
    ArrayList<NewWork> a;
    private FirebaseAuth firebaseAuth;
    String userEmail;

    public CustomAdapterMyWorks(Context c, ArrayList<NewWork> a) {
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
        final NewWork s= (NewWork) this.getItem(position);

        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.task_myworks_final,parent,false);
        }

        TextView textViewTaskName = (TextView) convertView.findViewById(R.id.myWorkTaskName);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.myWorkStatus);
        TextView textViewtime = (TextView) convertView.findViewById(R.id.myWorkDate);
        TextView textViewtime2 = (TextView) convertView.findViewById(R.id.myWorkTaskDuration);


        textViewtime2.setText("Deadline :");
        textViewTaskName.setText(s.get_work_name());
        textViewStatus.setText(s.get_status());
        textViewtime.setText(s.get_time() +"  "+s.get_date());

        return convertView;
    }
}