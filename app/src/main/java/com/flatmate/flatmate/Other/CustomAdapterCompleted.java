package com.flatmate.flatmate.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;

import java.util.ArrayList;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterCompleted extends BaseAdapter{

    Context c;
    ArrayList<NewWork> a;

    public CustomAdapterCompleted(Context c, ArrayList<NewWork> a) {
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
            convertView= LayoutInflater.from(c).inflate(R.layout.task_completed_final,parent,false);
        }

        TextView completedNameOfTask = (TextView) convertView.findViewById(R.id.completedNameOfTask);
        TextView completedUserName = (TextView) convertView.findViewById(R.id.completedUserName);
        TextView completedCredits = (TextView) convertView.findViewById(R.id.completedCredits);
        TextView completedDate = (TextView) convertView.findViewById(R.id.completedDate);


        final NewWork s= (NewWork) this.getItem(position);

        completedNameOfTask.setText(s.get_work_name());
        completedUserName.setText(s.get_userEmail());
        completedDate.setText(s.get_date());
        completedCredits.setText(s.get_credits());

        return convertView;
    }
}