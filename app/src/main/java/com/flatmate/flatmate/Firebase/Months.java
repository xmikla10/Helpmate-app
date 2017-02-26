package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class Months
{
    //name and address string
    private String month;
    private String membersCount;

    public Months()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_month() {
        return month;
    }

    public void set_month(String month) {
        this.month = month;
    }

    public String get_membersCount() {
        return membersCount;
    }

    public void set_membersCount(String membersCount) {
        this.membersCount = membersCount;
    }


}
