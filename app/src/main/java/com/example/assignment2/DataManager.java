package com.example.assignment2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class DataManager implements Serializable {

    String webJSON; // Stores the JSON String returned from webserver
    DatabaseHelper myDb; // The DB connection object
    ArrayList<Task> tasksArrayList; // Stores all Task objects
    Context mContext;

    public static final String TAG = "DATA_MANAGER";

    //Default Constructor for the DataManager
    public DataManager(Context context) {

        mContext = context;
        myDb = new DatabaseHelper( mContext );
    }



    public void clearDataBase(){
        myDb.clearDataBase();
    }


    //Convert the JSON into readable chunks.
    public void populateDatabaseWithResult( String result ) throws JSONException {

        webJSON = result;

        JSONObject obj = new JSONObject(webJSON);
        JSONArray tasks = obj.getJSONArray("tasks");
        JSONArray clients = obj.getJSONArray("clients");


        // Super lazy way of adding JSON data into DB/Arraylists
        for( int i = 0; i < tasks.length(); i++ ) {

            myDb.insertTaskData(
                    (int) tasks.getJSONObject(i).getInt("task_id"),
                    tasks.getJSONObject(i).getString("task_desc"),
                    tasks.getJSONObject(i).getString("deadline"),
                    (int) tasks.getJSONObject(i).getInt("client_id")
            );
        }

        for( int i = 0; i < clients.length(); ++i ) {

            myDb.insertClientData(
                    (int) clients.getJSONObject(i).getInt("client_id"),
                    clients.getJSONObject(i).getString("name"),
                    clients.getJSONObject(i).getString("address"),
                    clients.getJSONObject(i).getString("birth_date"),
                    clients.getJSONObject(i).getString("photo_url")
            );
        }

        buildTasksArrayList();

    }


    // Takes the database data and builds and arraylist of task data.
    public void buildTasksArrayList() {

        tasksArrayList = new ArrayList<Task>();

        Cursor res = myDb.getCompleteTaskList();

        if(res.getCount() == 0){
            Log.d(TAG, "No data returned from getCompleteTaskList function call");
            return;
        }

        while( res.moveToNext() ) {
            Task singleTask = new Task(
                    Integer.parseInt(res.getString(0)),
                    res.getString(1),
                    res.getString(2),
                    Integer.parseInt(res.getString(3)),
                    res.getString(4),
                    res.getString(5),
                    res.getString(6),
                    res.getString(7)
            );

            tasksArrayList.add(singleTask);
        }

    }

    public ArrayList<Client> getClientsArrayList() {
        ArrayList<Client> clientsArrayList = new ArrayList<Client>();

        Cursor res = myDb.getCompleteClientList();

        if(res.getCount() == 0){
            Log.d(TAG, "No data returned from getCompleteClientkList function call");
        }

        while( res.moveToNext() ) {
            Client singleClient = new Client(
                    Integer.parseInt(res.getString(0)),
                    res.getString(1)
            );

            clientsArrayList.add(singleClient);

        }

        return clientsArrayList;
    }

    public String descriptionFromID( int id ) {

        Cursor res = myDb.getTaskDescription( id );
        res.moveToNext();
        return res.getString(0);
    }

    public String deadlinenFromID( int id ) {

        Cursor res = myDb.getTaskDeadline( id );
        res.moveToNext();
        return res.getString(0);
    }

    public int clientIDFromID( int id ) {

        Cursor res = myDb.getTaskClientID( id );
        res.moveToNext();
        return Integer.parseInt(res.getString(0));
    }

    public void updateByID( int id, String description, String deadline ) {
        myDb.updateTaskFromDatabase(id, description, deadline);
    }

    public void removeByID( int id ) {
        myDb.deleteTaskFromDatabase( id );
    }

    public void updateTaskClientByID( int id, int newClientID ) {
        myDb.updateTaskClientFromDatabase( id, newClientID );
    }

    public String addressFromID(int clientID ) {
        Cursor res = myDb.getClientAddress( clientID );
        res.moveToNext();
        return res.getString( 0 );
    }



    // Getter for taskArrayList data.
    public ArrayList<Task> getTaskArrayList(){ return tasksArrayList; }


}
