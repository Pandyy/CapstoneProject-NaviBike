package com.example.pandrew.capstoneproj;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{
        private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private GoogleMap mMap;
        EditText ed1;
        ToggleButton tb1;
        Button b1;
        Button b2;
        LatLng origin;
        LatLng destinationF;
        double latBegin;
        double longBegin;
        double latEnd;
        double longEnd;
        double updateLat;
        double updateLng;
        Location location;
        Location UPDATES;
        LatLng updates;
        LocationManager LM;
        Geocoder geocoder;
        List<Address> addresses;
        List<Address> destinationAddresses;
        Address add;
        Address dest;
        String address;
        String destination;
        String urlResponses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        b1 = (Button) findViewById(R.id.getDirections);
        b2 = (Button) findViewById(R.id.headLight);
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
                headlights();
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
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getParent(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    UPDATES = LM.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    updateLat = UPDATES.getLatitude();
                    updateLng = UPDATES.getLongitude();
                    updates = new LatLng(updateLat,updateLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updates,17));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 30, locationListener);
    }

    public void getDirections() {
        mMap.clear();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            getCurrentLoc();
            LatLng START1 = new LatLng(latBegin,longBegin);
            mMap.addMarker(new MarkerOptions().position(START1).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(START1,17));
        }
        if(ed1.getText().toString().isEmpty()){
            Toasty("Please Enter a Destination!!!");
        }
        else{
            origin = new LatLng(latBegin,longBegin);
            destinationF = getDestinationCoordinates();
            String getURL = getRequestURL(origin,destinationF);
            reqDirectionsTask reqDirectionsTask = new reqDirectionsTask();
            reqDirectionsTask.execute(getURL);
        }
    }

    private String getRequestURL(LatLng origin, LatLng destination) {
        String oriStr = "origin=" + origin.latitude + "," + origin.longitude;
        String destStr = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=bicycling";
        String key = "key=AIzaSyCRLKfTgA_W6rOcJI9EjL1uTXNlIzL-GP0";
        String param = oriStr + "&" + destStr + "&" + sensor + "&" + mode + "&" + key;
        String url = "https://maps.googleapis.com/maps/api/directions/" + "json?" + param;
        return url;
    }

    public void headlights(){
        Intent intent = new Intent(this, Headlights.class);
        startActivity(intent);
    }
    private String requestDirection(String requestURL) throws IOException {
        String Response = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(requestURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String steps = "";
            while((steps = bufferedReader.readLine()) != null){
                stringBuffer.append(steps);
            }

            Response = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return Response;
    }

    public class reqDirectionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            urlResponses = "";
            try {
                urlResponses = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return urlResponses;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionsJsonParser directionsParser = new DirectionsJsonParser();
                routes = directionsParser.parse(jsonObject);
            } catch(JSONException e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for(List<HashMap<String, String>> path : lists ){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat,lng));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions!=null){
                mMap.addPolyline(polylineOptions);
            }else{
                Toasty("Sorry, directions not found!");
            }
        }
    }
    public void getCurrentLoc(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = LM.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        latBegin = location.getLatitude();
        longBegin = location.getLongitude();
        try {
            addresses = geocoder.getFromLocation(latBegin, longBegin, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toasty("Can not geocode");
        }
        add = addresses.get(0);
        address = add.getAddressLine(0);
        return;
    }
    public LatLng getDestinationCoordinates(){
        try {
            destinationAddresses = geocoder.getFromLocationName(ed1.getText().toString(), 1);
            if(destinationAddresses.size() == 0){
                Toasty("Could not find coordinates of destination");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toasty("Can not geocode");
        }
        dest = destinationAddresses.get(0);
        latEnd = dest.getLatitude();
        longEnd = dest.getLongitude();
        LatLng finaldest = new LatLng(latEnd, longEnd);
        destination = dest.getAddressLine(0);
        mMap.addMarker(new MarkerOptions().position(finaldest).title(destination));
        return finaldest;
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
        Toast.makeText(this,S,Toast.LENGTH_LONG).show();
    }
}
