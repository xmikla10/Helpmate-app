package com.flatmate.flatmate.Firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class FirebaseHelperUser
{
    DatabaseReference db;
    Boolean saved=null;
    ArrayList<NewUser> a =new ArrayList<>();

    public FirebaseHelperUser(DatabaseReference db) {
        this.db = db;
    }
    //WRITE
    public Boolean save(NewUser newUser)
    {
        if(newUser == null)
        {
            saved=false;
        }else
        {
            try
            {
                db.child("user").child("users").child(newUser.get_ID()).child("data").push().setValue(newUser);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }
        }
        return saved;
    }


}