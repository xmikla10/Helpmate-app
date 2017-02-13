package com.flatmate.flatmate.Firebase;

/**
 * Created by Peter on 11/20/2016.
 */

public class Members
{
    //name and address string
    private String membersCount;

    public Members()
    {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String get_membersCount() {
        return membersCount;
    }

    public void set_membersCount(String membersCount) {
        this.membersCount = membersCount;
    }


}
