package com.flatmate.flatmate.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;

import java.util.ArrayList;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterFindGroup extends BaseAdapter{

    Context c;
    ArrayList<NewGroup> a;

    public CustomAdapterFindGroup(Context c, ArrayList<NewGroup> a) {
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
            convertView= LayoutInflater.from(c).inflate(R.layout.task_find_group,parent,false);
        }

        TextView textSearchEmail = (TextView) convertView.findViewById(R.id.textSearchEmail);
        TextView textSearchGroup = (TextView) convertView.findViewById(R.id.textSearchGroup);

        final NewGroup s= (NewGroup) this.getItem(position);

        textSearchEmail.setText(s.get_user_email());
        textSearchGroup.setText(s.get_group_name());

        return convertView;
    }
}