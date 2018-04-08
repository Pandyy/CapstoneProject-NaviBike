package com.example.pandrew.capstoneproj;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{
        private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private GoogleMap mMap;
        EditText ed1;
        String destination;
        ToggleButton tb1;
        Button b1;
        Button b2;
        Button b3;
        LatLng origin;
        LatLng destinationF;
        double latBegin;
        double longBegin;
        double latEnd;
        double longEnd;
        Location location;
        LocationManager LM;
        Geocoder geocoder;
        List<Address> addresses;
        List<Address> destinationAddresses;
        Address add;
        String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        b1 = (Button) findViewById(R.id.getDirections);
        b2 = (Button) findViewById(R.id.Other);
        b3 = (Button) findViewById(R.id.headLight);
        tb1 = (ToggleButton) findViewById(R.id.SATV);
        ed1 = (EditText) findViewById(R.id.destination);
        geocoder = new Geocoder(this);
        LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirections();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        tb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tb1.isChecked()){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
    }

    public void getDirections() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            getCurrentLoc();
        }
        if(ed1.getText().toString().isEmpty()){
            Toasty("Please Enter a Destination!!!");
        }
        else{
            origin = new LatLng(latBegin,longBegin);
            //destinationF = new LatLng(latEnd,longEnd);
            //NEED TO GEOCODE THE DESTINATION INTO LATLNG COORDINATES
            getRequestURL(origin,destinationF);
        }
    }

    private String getRequestURL(LatLng origin, LatLng destination) {
        String oriStr = "origin=" + origin.latitude + "," + origin.longitude;
        String destStr = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=bicycling";
        String param = oriStr + "&" + destStr + "&" + sensor + "&" + mode;
        String url = "http://maps.googleapis.com/maps/apis/directions/" + "json?" + param;
        return url;
    }

    public void getCurrentLoc(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LM.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        latBegin = location.getLatitude();
        longBegin = location.getLongitude();
        LatLng START1 = new LatLng(latBegin,longBegin);
        try {
            addresses = geocoder.getFromLocation(latBegin, longBegin, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toasty("Can not geocode");
        }
        add = addresses.get(0);
        address = add.getAddressLine(0);
        mMap.addMarker(new MarkerOptions().position(START1).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(START1,17));
        return;
    }
    public void getDestinationCoordinates(){
        /*
        try {
            addresses = geocoder.getFromLocation(latBegin, longBegin, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toasty("Can not geocode");
        }
         */
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LM.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        // Add a marker in Sydney and move the camera
        LatLng destinationStart = new LatLng(40.524874, -74.4356713);
        mMap.addMarker(new MarkerOptions().position(destinationStart).title("Starting Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationStart,17));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMyLocationEnabled(true);
    }

    public void onMapLoaded(){

    }

    public void Toasty(String S){
        Toast.makeText(this,S,Toast.LENGTH_SHORT).show();
    }
}
