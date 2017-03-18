package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NotificationMessage
{
    //name and address string
    private String group_name;
    private String message;
    private String date;
    private String work_ID;
    private String random;

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

    public String get_date()
    {
        return date;
    }

    public void set_date(String date)
    {
        this.date = date;
    }

    public String get_work_ID()
    {
        return work_ID;
    }

    public void set_work_ID(String work_ID)
    {
        this.work_ID = work_ID;
    }

    public String get_random()
    {
        return random;
    }

    public void set_random(String random)
    {
        this.random = random;
    }


}
