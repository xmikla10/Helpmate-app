package com.flatmate.flatmate.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flatmate.flatmate.Firebase.NewBid;
import com.flatmate.flatmate.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Oclemy on 6/21/2016 for ProgrammingWizards Channel and http://www.camposha.com.
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class CustomAdapterAuction extends BaseAdapter{

    Context c;
    ArrayList<NewBid> a;

    public CustomAdapterAuction(Context c, ArrayList<NewBid> a) {
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
        final NewBid s= (NewBid) this.getItem(position);

        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.task_auction_bid,parent,false);
        }

        TextView textViewCredits = (TextView) convertView.findViewById(R.id.auctionCredits);
        TextView textViewName = (TextView) convertView.findViewById(R.id.auctionName);

        String pom;
        String work_name = "";

        if(s.get_userName().length() > 20)
        {
            pom = s.get_userName();
            char[] charArray = pom.toCharArray();
            for( int i = 0; i < 20; i++)
            {
                work_name = work_name + String.valueOf(charArray[i]);
            }
            work_name = work_name + "...";
        }
        else
            work_name = s.get_userName();

        textViewName.setText(work_name);

        if( s.get_credits().equals("1"))
        {
            textViewCredits.setText(s.get_credits() + c.getString(R.string.credit));
        }
        else if ( s.get_credits().equals("not interested"))
        {
            textViewCredits.setText(c.getString(R.string.not_interested));
        }
        else
            textViewCredits.setText(s.get_credits() + c.getString(R.string.credits));

        return convertView;
    }
}