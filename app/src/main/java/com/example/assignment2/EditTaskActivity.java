package com.example.assignment2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EditTaskActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private Button btnUpdate, btnDelete;
    private EditText editTask, editDeadline;
    private Spinner clientSpinner;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng latLng;

    private int taskID, position, clientID;

    private String userAddress;

    DataManager mDataManager;
    ArrayList<Client> clientArrayList;


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final String TAG = "EDIT_TASK_ACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_layout);

        btnUpdate = (Button) findViewById(R.id.updateButton);
        btnDelete = (Button) findViewById(R.id.deleteButton);

        editTask = (EditText) findViewById(R.id.editTask);
        editDeadline = (EditText) findViewById( R.id.editDeadline);

        clientSpinner = (Spinner) findViewById( R.id.clientSpinner );


        Intent i = getIntent();
        taskID = i.getIntExtra("id", taskID);

        mDataManager = new DataManager(this);
        clientID = mDataManager.clientIDFromID( taskID );
        clientArrayList = mDataManager.getClientsArrayList();

        editTask.setText( mDataManager.descriptionFromID( taskID ) );
        editDeadline.setText( mDataManager.deadlinenFromID( taskID ));

        ArrayAdapter<Client> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientArrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientSpinner.setAdapter( spinnerAdapter );
        clientSpinner.setSelection(setSpinnerPosition());

        getLocationPermission();

        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Client client = (Client) clientSpinner.getSelectedItem();


                String description = editTask.getText().toString();
                String deadline = editDeadline.getText().toString();

                if(!description.equals("") && !deadline.equals("")) {
                    mDataManager.updateByID(taskID, description, deadline);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid Update Value", Toast.LENGTH_SHORT);
                    toast.show();
                }

                if( client.getId() != clientID ) {
                    mDataManager.updateTaskClientByID(taskID, client.getId() );
                    clientID = client.getId();
                    setMapView();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataManager.removeByID( taskID );

                finish();
            }
        });

    }

    private int setSpinnerPosition() {

        int pos;

        for (pos = 0; pos < clientArrayList.size(); ++pos ){

            if ( clientArrayList.get(pos).getId() == clientID ) {
                break;
            }

        }

        return pos;
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );
    }

    private void getLocationPermission() {
        String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission( this.getApplicationContext(), COURSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private Address getUserLocation( ) {


        userAddress = mDataManager.addressFromID(clientID);

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<Address>();

        try {
            list = geocoder.getFromLocationName( userAddress, 1 );
        } catch ( IOException e) {
            Log.e("GeoCoder", e.getMessage());
        }

        if (list.size() > 0 ) {
            Address address = list.get(0);

            return address;
        }
        return null;
    }

    public void setMapView() {
        Address address = getUserLocation();
        latLng = new LatLng(address.getLatitude(), address.getLongitude() );

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( latLng, 15 ));
        mMap.setMyLocationEnabled( true );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED) {

            if( 4000 > SphericalUtil.computeDistanceBetween(
                    new LatLng(
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()), latLng) ) {

                Toast.makeText(this, "You are near this client", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setMapView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch( requestCode ) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if( grantResults.length > 0 ) {
                    for(int i = 0; i < grantResults.length; i++ ) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED ) {
                            return;
                        }
                    }
                    initMap();
                }
            }
        }
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

