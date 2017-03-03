package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class AddMembers
{
    //name and address string
    private String group_ID;
    private String sender_email;
    private String group_name;

    public AddMembers()
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

    public String get_sender_email() {
        return sender_email;
    }

    public void set_sender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public String get_group_name() {
        return group_name;
    }

    public void set_group_name(String group_name) {
        this.group_name = group_name;
    }


}
