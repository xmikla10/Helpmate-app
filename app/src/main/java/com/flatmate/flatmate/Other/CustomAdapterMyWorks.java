package com.flatmate.flatmate.Other;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        TextView circleLetter = (TextView) convertView.findViewById(R.id.myWorkcircleLetter);
        GradientDrawable gd = (GradientDrawable) circleLetter.getBackground();
        ImageView im = (ImageView) convertView.findViewById(R.id.arrowMyWorks);


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

        textViewtime2.setText(R.string.deadline);
        textViewTaskName.setText(work_name);

        MyStatus statusC = new MyStatus();
        String statusInString = statusC.setStatus(s.get_status(), c);

        textViewStatus.setText(statusInString);

        textViewtime.setText(s.get_time() +"  "+s.get_date());

        if (s.get_status().equals("5"))
        {
            textViewTaskName.setTextColor(Color.BLACK);
            textViewStatus.setTextColor(Color.RED);
            textViewtime2.setText("");
            textViewtime.setText("");
            im.setVisibility(View.INVISIBLE);
        }

        String firstLetter = work_name.substring(0,1).toUpperCase();

        switch (firstLetter)
        {
            case "A":case "B":case "C":case "D":
            gd.setColor(Color.RED); break;
            case "E":case "F":case "G":case "H": //blue
            gd.setColor(Color.parseColor("#0091EA")); break;
            case "I":case "J":case "K":case "L": //green
            gd.setColor(Color.parseColor("#20d8bf")); break;
            case "M":case "N":case "O":case "P": // green
            gd.setColor(Color.parseColor("#36b201")); break;
            case "Q":case "R":case "S":case "T":  //fialova
            gd.setColor(Color.parseColor("#7C4DFF")); break;
            case "U":case "V":case "W":case "X": // ruzova
            gd.setColor(Color.parseColor("#FF80AB")); break;
            case "Y":case "Z":
            gd.setColor(Color.parseColor("#1e09fa")); break;
            default: //oranzova
                gd.setColor(Color.parseColor("#FB7B0A")); break;

        }

        circleLetter.setText(firstLetter);

        return convertView;
    }
}