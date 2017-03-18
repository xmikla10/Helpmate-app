package com.flatmate.flatmate.Other;

import android.content.Context;
import android.content.res.Resources;

import com.flatmate.flatmate.R;

/**
 * Created by enterprise on 18.3.17.
 */

public class MyStatus
{

    public MyStatus() {}

    public String setStatus(String statusNumber, Context c)
    {
        String returnStatus;

        System.out.println("--------->" + statusNumber);

        if(statusNumber.equals("1"))
        {
            returnStatus = c.getString(R.string.status_auctioning);
        }
        else if(statusNumber.equals("2"))
        {
            returnStatus = c.getString(R.string.status_progress);
        }
        else if(statusNumber.equals("3"))
        {
            returnStatus = c.getString(R.string.status_done);
        }
        else if(statusNumber.equals("4"))
        {
            returnStatus = c.getString(R.string.status_unauctioned);
        }
        else if(statusNumber.equals("5"))
        {
            returnStatus = c.getString(R.string.status_uncompleted);
        }
        else
            returnStatus = "";

        return returnStatus;
    }
}
