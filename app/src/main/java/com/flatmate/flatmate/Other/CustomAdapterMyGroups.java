package com.flatmate.flatmate.Other;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.NewGroup;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterMyGroups extends BaseAdapter{

    private FirebaseAuth firebaseAuth;
    private String groupID;
    String groupS;
    Context c;
    ArrayList<NewGroup> a;
    DatabaseReference db;
    String userID;
    TextView textGroupName;

    public CustomAdapterMyGroups(Context c, ArrayList<NewGroup> a, String group) {
        groupID = group;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final NewGroup s= (NewGroup) this.getItem(position);

        db = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid().toString();

        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.task_my_groups,parent,false);
        }

        textGroupName = (TextView) convertView.findViewById(R.id.textGroupName);

        String pom;
        String group_name = "";

        if(s.get_group_name().length() > 20)
        {
            pom = s.get_group_name();
            char[] charArray = pom.toCharArray();
            for( int i = 0; i < 20; i++)
            {
                group_name = group_name + String.valueOf(charArray[i]);
            }
            group_name = group_name + "...";
        }
        else
            group_name = s.get_group_name();

        textGroupName.setText(s.get_group_name());

        groupS = s.get_group_ID();
        if (groupID.equals(groupS))
        {
            ImageView image = (ImageView) convertView.findViewById(R.id.groupImage);
            image.setImageResource(R.drawable.accept_group_green);
        }
        else
        {
            ImageView image = (ImageView) convertView.findViewById(R.id.groupImage);
            image.setImageResource(R.drawable.accept_group_grey);
        }

        return convertView;
    }
}