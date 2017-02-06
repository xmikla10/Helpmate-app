package com.flatmate.flatmate.Other;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;

import java.util.ArrayList;
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

        textViewTaskName.setText(work_name);
        textViewStatus.setText(s.get_status());
        textViewtime2.setText(s.get_time() +"  "+s.get_date());

        return convertView;
    }
}