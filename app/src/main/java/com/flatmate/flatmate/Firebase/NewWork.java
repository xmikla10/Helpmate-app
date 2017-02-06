package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class NewWork {
    //name and address string
    private String work_name;
    private String duration;
    private String deadline;
    private String time;
    private String date;
    private String status;
    private String bidsID;
    private String userEmail;
    private String credits;

    public NewWork()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_work_name() {
        return work_name;
    }

    public void set_work_name(String work_name) {
        this.work_name = work_name;
    }

    public String get_duration()
    {
        return duration;
    }

    public void set_duration(String duration)
    {
        this.duration = duration;
    }

    public String get_deadline()
    {
        return deadline;
    }

    public void set_deadline(String deadline)
    {
        this.deadline = deadline;
    }

    public String get_time()
    {
        return time;
    }

    public void set_time(String time)
    {
        this.time = time;
    }

    public String get_date()
    {
        return date;
    }

    public void set_date(String date)
    {
        this.date = date;
    }

    public String get_status()
    {
        return status;
    }

    public void set_status(String status)
    {
        this.status = status;
    }

    public String get_bidsID()
    {
        return bidsID;
    }

    public void set_bidsID(String bidsID)
    {
        this.bidsID = bidsID;
    }

    public String get_userEmail()
    {
        return userEmail;
    }

    public void set_userEmail(String userEmail)
    {
        this.userEmail = userEmail;
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
