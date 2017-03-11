package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NotificationMessage
{
    //name and address string
    private Integer control;
    private String message;

    public NotificationMessage()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public Integer get_control() {
        return control;
    }

    public void set_control(Integer control) { this.control = control;}

    public String get_message()
    {
        return message;
    }

    public void set_message(String message)
    {
        this.message = message;
    }


}
