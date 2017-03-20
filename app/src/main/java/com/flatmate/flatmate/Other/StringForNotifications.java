package com.flatmate.flatmate.Other;

import android.content.Context;

import com.flatmate.flatmate.R;

/**
 * Created by enterprise on 18.3.17.
 */

public class StringForNotifications
{

    public StringForNotifications() {}

    /**
     * control : - 1 = AddNewWork
     *           - 2 = New user added
     *           - 3 = User removed
     *           - 4 = Work win
     *           - 5 = Work done
     *           - 6 = Work change status
     *           - 8 = Work change status for every members
     */

    public String setNotificationString(String notifNumber, String addToNotif,  Context a)
    {
        String returnNotif;

        if(notifNumber.equals("1"))
        {
            returnNotif = a.getString(R.string.new_work_added) + addToNotif;
        }
        else if(notifNumber.equals("2"))
        {
            returnNotif = a.getString(R.string.new_user_added) + addToNotif;
        }
        else if(notifNumber.equals("3"))
        {
            returnNotif = a.getString(R.string.user) + addToNotif + a.getString(R.string.removed_from_group);
        }
        else if(notifNumber.equals("4"))
        {
            returnNotif = a.getString(R.string.you_win_auction) + addToNotif;
        }
        else if(notifNumber.equals("5"))
        {
            returnNotif = a.getString(R.string.work) + addToNotif + a.getString(R.string.done);
        }
        else if(notifNumber.equals("6"))
        {
            returnNotif = a.getString(R.string.work) + addToNotif + a.getString(R.string.change_status);
        }
        else if(notifNumber.equals("7"))
        {
            returnNotif = a.getString(R.string.the_group) + addToNotif + a.getString(R.string.was_renamed);
        }
        else if(notifNumber.equals("8"))
        {
            returnNotif = a.getString(R.string.work) + addToNotif + a.getString(R.string.change_status);
        }
        else if(notifNumber.equals("9"))
        {
            returnNotif = a.getString(R.string.invited_new_group) + addToNotif;
        }
        else
            returnNotif = "";

        return returnNotif;
    }
}
