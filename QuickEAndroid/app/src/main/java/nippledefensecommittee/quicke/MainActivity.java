package nippledefensecommittee.quicke;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.instantapps.ActivityCompat;
import com.google.android.gms.instantapps.PackageManagerCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_CODE = 123;

    private FusedLocationProviderClient locationClient;
    private double longitude;
    private double latitude;
    private String locStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(handlePermissions() == true) {
            locationClient = LocationServices.getFusedLocationProviderClient(this);
            locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        locStr = "LONG: " + location.convert(longitude, Location.FORMAT_DEGREES) + " LAT: " + location.convert(latitude, Location.FORMAT_DEGREES);
                        Log.d("Location", locStr);
                    }
                }
            });
        }
    }

    private boolean handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] locPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(locPermission, LOCATION_CODE);
            return false;
        }
        else {
            //getLocation();
            return true;
        }
    }


    protected Location getLocation() {
        FusedLocationProviderClient locationClient;
        double longitude;
        double latitude;
        String locStr;

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    locStr = "LONG: " + location.convert(longitude, Location.FORMAT_DEGREES) + " LAT: " + location.convert(latitude, Location.FORMAT_DEGREES);
                    Log.d("Location", locStr);

                }
            }
        });
    }



}
