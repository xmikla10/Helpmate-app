package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NotificationMessage
{
    //name and address string
    private String group_name;
    private String message;

    public NotificationMessage()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_group_name() {
        return group_name;
    }

    public void set_group_name(String group_name) { this.group_name = group_name;}

    public String get_message()
    {
        return message;
    }

    public void set_message(String message)
    {
        this.message = message;
    }


}