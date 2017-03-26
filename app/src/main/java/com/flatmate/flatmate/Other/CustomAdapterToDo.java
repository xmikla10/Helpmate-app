package com.flatmate.flatmate.Other;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flatmate.flatmate.Activity.MainActivity;
import com.flatmate.flatmate.Firebase.NewWork;
import com.flatmate.flatmate.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        TextView circleLetter = (TextView) convertView.findViewById(R.id.circleLetter);
        GradientDrawable gd = (GradientDrawable) circleLetter.getBackground();
        ImageView im = (ImageView) convertView.findViewById(R.id.arrowShowAuction);

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
            im.setVisibility(View.VISIBLE);
        }
        else if(statusInString.equals(c.getString(R.string.status_progress)))
        {
            textViewtime2.setText(cutUserName(s.get_bidsLastUserName()));
            textViewTaskName.setTextColor(Color.BLACK);
            textViewtime.setText(R.string.who_won_1);
            textViewStatus.setTextColor(Color.BLACK);
            textViewtime.setTextSize(14);
            im.setVisibility(View.VISIBLE);

        }
        else if(statusInString.equals(c.getString(R.string.status_done)))
        {
            textViewtime2.setText(cutUserName(s.get_bidsLastUserName()));
            textViewTaskName.setTextColor(Color.BLACK);
            textViewtime.setText(R.string.work_completed_by_1);
            textViewtime.setTextSize(12);
            textViewStatus.setTextColor(Color.parseColor("#ff669900"));
            im.setVisibility(View.INVISIBLE);

        }
        else if(statusInString.equals(c.getString(R.string.status_unauctioned)))
        {
            textViewtime2.setText("");
            textViewtime.setText(R.string.delete_ask);
            textViewStatus.setTextColor(Color.RED);
            textViewTaskName.setTextColor(Color.BLACK);
            im.setVisibility(View.INVISIBLE);

        }
        else if(statusInString.equals(c.getString(R.string.status_uncompleted)))
        {
            textViewtime2.setText(cutUserName(s.get_bidsLastUserName()));
            textViewtime.setText(R.string.wrk_uncompleted_by);
            textViewStatus.setTextColor(Color.RED);
            textViewTaskName.setTextColor(Color.BLACK);
            im.setVisibility(View.INVISIBLE);

        }
        else
            textViewtime2.setText(s.get_deadline());

        textViewTaskName.setText(work_name);
        textViewStatus.setText(statusInString);

        String firstLetter = work_name.substring(0,1).toUpperCase();

        switch (firstLetter)
        {
            case "A":case "B":case "C":case "D":
                gd.setColor(Color.RED); break;
            case "E":case "F":case "G":case "H":
                gd.setColor(Color.parseColor("#0091EA")); break;
            case "I":case "J":case "K":case "L":
                gd.setColor(Color.parseColor("#689F38")); break;
            case "M":case "N":case "O":case "P":
                gd.setColor(Color.parseColor("#E64A19")); break;
            case "Q":case "R":case "S":case "T":
                gd.setColor(Color.parseColor("#7C4DFF")); break;
            case "U":case "V":case "W":case "X":
                gd.setColor(Color.parseColor("#FF80AB")); break;
            case "Y":case "Z":
                gd.setColor(Color.parseColor("#EF6C00")); break;
            default:
                gd.setColor(Color.parseColor("#FB7B0A")); break;

        }

        circleLetter.setText(firstLetter);

        return convertView;
    }

    public String cutUserName(String name)
    {
        String pom1 = "";
        if( name.length() > 12)
        {
            char[] charArray = name.toCharArray();
            for( int i = 0; i < 12; i++)
            {
                pom1= pom1 + String.valueOf(charArray[i]);
            }
            pom1 = pom1 + "...";
        }
        else
            pom1 = name;

        return pom1;
    }
}