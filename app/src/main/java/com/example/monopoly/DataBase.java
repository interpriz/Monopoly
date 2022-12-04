package com.example.monopoly;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private FirebaseDatabase database;

    private List<String> users = new ArrayList<>();

    // сингелтон
    public static DataBase instance = null;

    public static DataBase getInstance()
    {
        if (instance == null) {
            instance = new DataBase();
            return instance;
        }
        else
            return instance;
    }

    public DataBase() {
        this.database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public DatabaseReference getRef(String sourse){
        return database.getReference(sourse);
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public List<String> getUsers() {
        return users;
    }
}
