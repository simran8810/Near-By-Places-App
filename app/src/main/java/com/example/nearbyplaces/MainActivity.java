/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nearbyplaces;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Location sample.
 *
 * Demonstrates use of the Location API to retrieve the last known location for a device.
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * See https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart if you are
 * also using APIs that need authentication.
 */
public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback,
        OnInfoWindowClickListener {



    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    GoogleMap googleMap;
    LatLng latLng;
    MarkerOptions markerOptions;
    protected MapFragment mapFragment;
    public final static String EXTRA_MESSAGE = "com.example.nearbyplaces";
    double lati,longi;

    final double RADIUS = 0.001797;

    final String[] jobsArrayTitles = {"out bound process","HR Executive","Data Entry Jobs","Software Engineer","Android Developer","Newspaper editor","Musician","Professor","Your location"};

    final String[] jobsArrayDescription = {"Location:Delhi Excellent opportunity for you Looking for tele caller Fresher and experience candidate both can apply. Good salary+unlimited incentive Good listening and confidence.",
            "Manage Recruitment Job posting Initial screening Interviews Salary Negotiations.",
            "Part Time Home Based Data Entry Job, Home Based Typing Work, Home Based Form Filling Jobs, Home Based Copy paste Jobs, Part Time Home Based job,.",
            "Develop Solutions", "Develop applications", "Edit stuff","Play stuff","Do stuff","Your location"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        // Getting a reference to the map
        googleMap = mapFragment.getMap();

        // Getting reference to btn_find of the layout activity_main
        Button btn_find = (Button) findViewById(R.id.btn_find);

        buildGoogleApiClient();

        // Defining button click event listener for the find button
        OnClickListener findClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                EditText etLocation = (EditText) findViewById(R.id.et_location);

                // Getting user input location
                String location = etLocation.getText().toString();

                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };

        // Setting button click event listener for the find button
        btn_find.setOnClickListener(findClickListener);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lon = mLastLocation.getLongitude();

            lati = lat;
            longi = lon;
            //Marker my_location = map.addMarker(new MarkerOptions().position(location).title("My_location"));
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this,"no_location_detected", Toast.LENGTH_LONG).show();
        }




    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("connection error", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Connection error", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.setMyLocationEnabled(true);
        showMarkers(map);


    }
    public void showMarkers(GoogleMap map){
        map.clear();
        ArrayList<String> AllMarkers;

        String currentLocationMarker = lati + "," + longi;      //current location

        AllMarkers = createRandomMarkerPoints(lati,longi);

        AllMarkers.add(currentLocationMarker);      //adding current location to returned list of random array values

        final int latLongArraySize = AllMarkers.size();

        double [] myLatitudeArray = new double[latLongArraySize];
        double [] myLongitudeArray = new double[latLongArraySize];




        for(int i=0;i<AllMarkers.size();i++)
        {
            String[] tempArray = new String[]{};
            tempArray = AllMarkers.get(i).split(",");

            myLatitudeArray[i] = Double.parseDouble(tempArray[0].toString());
            myLongitudeArray[i] = Double.parseDouble(tempArray[1].toString());

        }

        ArrayList<Marker> markers = new ArrayList<>();

        for(int i =0;i<myLatitudeArray.length;i++) {
            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(myLatitudeArray[i], myLongitudeArray[i])).title(jobsArrayTitles[i]).snippet(jobsArrayDescription[i]));
            markers.add(marker);
           // map.setOnMarkerClickListener(this);
            map.setOnInfoWindowClickListener(this);
        }


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(Marker marker: markers) {

            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);


        map.moveCamera(cu);


    }
    public ArrayList<String> createRandomMarkerPoints(double lati,double longi){


        ArrayList<String> myArray=new ArrayList<>();
        int i = 8;
        final double pi = 3.14;

        while(i>0) {


            double randNum1 = Math.random();
            double randNum2 = Math.random();


            double w = RADIUS * Math.sqrt(randNum1);


            double t = 2 * pi * randNum2;

            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            double xNext = x / Math.cos(lati);



            double finalLongitude = xNext + longi;

            double finalLatitude = y + lati;

            String values=finalLatitude+","+finalLongitude;

            myArray.add(values);

            i--;

        }

        return myArray;
    }
    // An AsyncTask class for accessing the GeoCoding Web Service

//    @Override
//    public boolean onMarkerClick(final Marker marker) {
//        String mForecast = "hi_or_bye";
//        Intent intent = new Intent(this,DetailActivity.class);
//        intent.putExtra(EXTRA_MESSAGE, mForecast);
//        startActivity(intent);
//        return true;
//    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        // When touch InfoWindow on the market, display another screen./*
        String mForecast = marker.getTitle();
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE, mForecast);
        startActivity(intent);


        //getSupportFragmentManager().beginTransaction().add(R.id.container,new DetailActivity.DetailFragment()).commit();
    }
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map


            // Adding Markers on Google Map for each matching address
            for(int j=0;j<addresses.size();j++){

                Address address = (Address) addresses.get(j);
                lati = address.getLatitude();
                longi = address.getLongitude();
                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(lati, longi);

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                googleMap.addMarker(markerOptions);

                showMarkers(googleMap);


                // Locate the first location
                if(j==0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }

        }
    }

}




