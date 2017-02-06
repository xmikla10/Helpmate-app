package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NewBid
{
    //name and address string
    private String userName;
    private String credits;

    public NewBid()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_userName() {
        return userName;
    }

    public void set_userName(String userName) {
        this.userName = userName;
    }

    public String get_credits()
    {
        return credits;
    }

    public void set_credits(String credits)
    {
        this.credits = credits;
    }


}
