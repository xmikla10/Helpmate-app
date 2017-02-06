package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NewGroup
{
    //name and address string
    private String group_ID;
    private String group_name;
    private String user_name;
    private String user_email;
    private String user_ID;
    private String admin;

    public NewGroup()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_group_ID() {
        return group_ID;
    }

    public void set_group_ID(String group_ID) {
        this.group_ID = group_ID;
    }

    public String get_group_name()
    {
        return group_name;
    }

    public void set_group_name(String group_name)
    {
        this.group_name = group_name;
    }

    public String get_user_name()
    {
        return user_name;
    }

    public void set_user_name(String user_name)
    {
        this.user_name = user_name;
    }

    public String get_user_email()
    {
        return user_email;
    }

    public void set_user_email(String user_email)
    {
        this.user_email = user_email;
    }

    public String get_user_ID()
    {
        return user_ID;
    }

    public void set_user_ID(String user_ID)
    {
        this.user_ID = user_ID;
    }

    public String get_admin()
    {
        return admin;
    }

    public void set_admin(String admin)
    {
        this.admin = admin;
    }


}
