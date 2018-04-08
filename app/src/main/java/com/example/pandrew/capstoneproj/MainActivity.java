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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{
        private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private GoogleMap mMap;
        EditText ed1;
        Button b1;
        Button b2;
        Button b3;
        double latBegin;
        double longBegin;
        double latEnd;
        double longEnd;
        Location location;
        LocationManager LM;
        Geocoder geocoder;
        List<Address> addresses;
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
        geocoder = new Geocoder(this);
        LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnClk();
            }
        });

    }
    public void BtnClk() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            getCurrentLoc();
        }
    }

    public void getCurrentLoc(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LM.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        latBegin = location.getLatitude();
        longBegin = location.getLongitude();
        LatLng START = new LatLng(latBegin,longBegin);
        try {
            addresses = geocoder.getFromLocation(latBegin, longBegin, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toasty("Can not geocode");
        }
        add = addresses.get(0);
        address = add.getAddressLine(0);
        mMap.addMarker(new MarkerOptions().position(START).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(START,10));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LM.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        // Add a marker in Sydney and move the camera
        LatLng destinationStart = new LatLng(latBegin, longBegin);
        mMap.addMarker(new MarkerOptions().position(destinationStart).title("Starting Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationStart));
    }

    public void onMapLoaded(){

    }

    public void Toasty(String S){
        Toast.makeText(this,S,Toast.LENGTH_SHORT).show();
    }
}
