package com.flatmate.flatmate.Other;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.flatmate.flatmate.R;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class Pager extends FragmentPagerAdapter {

    //integer to count number of tabs
    private String fragments [] = {"My Works","To Do","Completed"};
    private Context context;


    public Pager(FragmentManager supportFragmentManager, Context applicationContext) {
        super(supportFragmentManager);
        context = applicationContext;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                return new Tab_MYWORKS();
            case 1:
                return new Tab_TODO();
            case 2:
                return new Tab_COMPLETED();
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return fragments.length;
    }

    public CharSequence getPageTitle(int position) {
        String fragments [] = {
                context.getString(R.string.my_works),
                context.getString(R.string.to_do),
                context.getString(R.string.completed)
        };

        return fragments[position];
    }
}