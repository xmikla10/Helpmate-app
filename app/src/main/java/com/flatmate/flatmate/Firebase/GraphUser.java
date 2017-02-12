package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class GraphUser
{
    //name and address string
    private String name;
    private String email;
    private String credits;
    private String ID;

    public GraphUser()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_email()
    {
        return email;
    }

    public void set_email(String email)
    {
        this.email = email;
    }

    public String get_ID()
    {
        return ID;
    }

    public void set_ID(String ID)
    {
        this.ID = ID;
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
