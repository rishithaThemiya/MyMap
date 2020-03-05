package com.example.mymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView weatherDes,weatherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initViews();
        initNetworkCall();
        initMyMap();
    }

    private void initMyMap(){
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocationGPS gps = new LocationGPS(getApplicationContext());
        Location loc = gps.getLocation();

        if(loc != null){
            double lati = loc.getLatitude();
            double longi = loc.getLongitude();
            Toast.makeText(getApplicationContext(),"Latitude :" + lati +"\n Longitude :" + longi,Toast.LENGTH_LONG).show();


            LatLng ICTFacLocation = new LatLng(lati,longi);
            googleMap.addMarker(new MarkerOptions().position(ICTFacLocation).title("Marker"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ICTFacLocation,10),5000,null);
        }
    }

    private void initViews(){
        weatherDes = findViewById(R.id.weather);
        weatherId = findViewById(R.id.id);
        weatherDes.setText("My weather goes here");
    }

    private void initNetworkCall(){
        MyWeatherService MWS = new MyWeatherService();

        MWS.execute("https://samples.openweathermap.org/data/2.5/weather?lat=50&lon=80&appid=b6907d289e10d714a6e88b30761fae22");

    }

    public class MyWeatherService extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Success", "onPreExecute: ");
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("test", "doInBackground: " + strings [0]);

            StringBuffer stringBuffer = null;

            try {
                URL url= new URL(strings[0]); //convert strings to url
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                //connection.connect();

                //int code = connection.getResponseCode();

                InputStream inputstream = connection.getInputStream();// to catch byte stream
                InputStreamReader inputStreamReader = new InputStreamReader(inputstream); // convert to character
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader); //
                stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line);

                }
                Log.d("NETWORK", "doInBackground: " + stringBuffer);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return stringBuffer.toString();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("update", "onProgressUpdate: ");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("post", "onPostExecute: ");
            super.onPostExecute(s);

            try {
                JSONObject response = new JSONObject(s);
                JSONArray weatherDeatilsArray = response.getJSONArray("weather");
                JSONObject weather = (JSONObject) weatherDeatilsArray.get(0);
                String description = weather.getString("description");
                String id = weather.getString("id");
                weatherId.setText(id);
                weatherDes.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
