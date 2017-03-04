package com.flatmate.flatmate.Other;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flatmate.flatmate.Activity.GroupInfoActivity;
import com.flatmate.flatmate.Activity.MyGroupsActivity;
import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterInfoGroup extends BaseAdapter{

    Context c;
    ArrayList<NewGroup> a;

    public CustomAdapterInfoGroup(Context c, ArrayList<NewGroup> a) {
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
            convertView= LayoutInflater.from(c).inflate(R.layout.task_info_members_final,parent,false);
        }

        TextView infoUserName = (TextView) convertView.findViewById(R.id.infoUserName);

        final NewGroup s= (NewGroup) this.getItem(position);

        System.out.println(s.get_user_name());

        infoUserName.setText(s.get_user_name());

        return convertView;
    }
}