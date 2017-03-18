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

        String pom;
        String pom1;
        String name = "";
        String work_name = "";

        if(s.get_bidsLastUserName().length() > 17)
        {
            pom = s.get_bidsLastUserName();
            char[] charArray = pom.toCharArray();
            for( int i = 0; i < 17; i++)
            {
                work_name = work_name + String.valueOf(charArray[i]);
            }
            name = name + "...";
        }
        else
            name = s.get_bidsLastUserName();

        if(s.get_work_name().length() > 23)
        {
            pom1 = s.get_work_name();
            char[] charArray = pom1.toCharArray();
            for( int i = 0; i < 23; i++)
            {
                work_name = work_name + String.valueOf(charArray[i]);
            }
            work_name = work_name + "...";
        }
        else
            work_name = s.get_work_name();

        completedNameOfTask.setText(work_name);
        completedUserName.setText(name);
        completedDate.setText(s.get_date());

        if( s.get_credits().equals("1"))
        {
            completedCredits.setText(s.get_credits() + " " + c.getString(R.string.credit));
        }
        else
            completedCredits.setText(s.get_credits() + " " + c.getString(R.string.credits));


        return convertView;
    }
}