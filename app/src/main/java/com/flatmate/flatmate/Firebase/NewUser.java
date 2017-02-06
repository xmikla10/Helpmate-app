package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NewUser
{
    //name and address string
    private String name;
    private String email;
    private String group;
    private String ID;

    public NewUser()
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

    public String get_group()
    {
        return group;
    }

    public void set_group(String group)
    {
        this.group = group;
    }

    public String get_ID()
    {
        return ID;
    }

    public void set_ID(String ID)
    {
        this.ID = ID;
    }


}
