package com.example.assignment2;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {

    public static final int DELETE_REQUEST = 1000;
    public static final int UPDATE_REQUEST = 2000;
    public static final String TAG = "MAIN_ACTIVITY";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    DataManager dataManager;
    TasksListAdapter adapter;
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[] { "www.yahoo.com" });






    }

    public boolean isServicesOK() {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this );

        if (available == ConnectionResult.SUCCESS ) {
            return true;
        } else if ( GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to Play services", Toast.LENGTH_SHORT).show();
        }

        return false;
    }



    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String response = "";

            BufferedReader br = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://192.168.1.73:8081/midp/hits");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                br = new BufferedReader(new InputStreamReader(inputStream));

                String line = null;
                while ((line = br.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    response += line;

                }

                if (response.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        br.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            // Clean up the json that was returned from the server
            JSONObject json = null;
            try {
                json = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                return response = json.toString(4);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Send it to onPostExecute() to be written to the view.
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            dataManager = new DataManager(getApplicationContext());
            dataManager.clearDataBase();

            try {

                dataManager.populateDatabaseWithResult( result );

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Print the ListView with data from Web Server
            mListView = (ListView) findViewById(R.id.TaskslistView);
            adapter = new TasksListAdapter(getApplicationContext(), R.layout.adapter_view_layout, dataManager.getTaskArrayList(), dataManager);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int taskID = (int) view.findViewById(R.id.description).getTag();

                    Intent editData = new Intent(getApplicationContext(), EditTaskActivity.class);
                    editData.putExtra("id", taskID);

                    if( isServicesOK() ) {
                        startActivityForResult(editData, DELETE_REQUEST);
                    }


                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case DELETE_REQUEST:

                dataManager.buildTasksArrayList();
                adapter = new TasksListAdapter(getApplicationContext(), R.layout.adapter_view_layout, dataManager.getTaskArrayList(), dataManager);
                mListView.setAdapter(adapter);
                break;

            default:
                Log.d(TAG, "Returned from unknown Activity");
        }

    }

}
