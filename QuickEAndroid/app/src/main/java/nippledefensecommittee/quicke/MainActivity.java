package nippledefensecommittee.quicke;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private static UserLocation userLoc = new UserLocation();
    private static FusedLocationProviderClient locationClient;
    private static final int LOCATION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAppBar();
        getUserPermission();
    }

    private void initializeAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
    }

    /**
     * Checks user permissions. If granted permission then call getUserLocation().
     * Otherwise, requests permission from user. Permission result handler takes care
     * of user response.
     */
    @TargetApi(23)
    private void getUserPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
            userLoc.setGrantedPermission(false);
        }
        else {
            getUserLocation();
        }
    }

    /**
     * User permission response handler.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation();
                }
                else {
                    // TODO: Handle user denying access to location

                    Toast.makeText(MainActivity.this, "Location services are unable to be used.", Toast.LENGTH_LONG).show();
                }
                break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Uses Google Play Services to obtain last known user location, which is usually
     * the user's current location. Populates fields of static UserLocation object.
     */
    private void getUserLocation() {
        locationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        try {
            locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location){
                    if (location != null) {
                        final double longitude = location.getLongitude();
                        final double latitude = location.getLatitude();

                        userLoc.setUserLocation(longitude, latitude);
                        userLoc.setUserLocationStrings(location.convert(longitude, Location.FORMAT_DEGREES), location.convert(latitude, Location.FORMAT_DEGREES));
                        userLoc.setGrantedPermission(true);
                    }
                }
            });
        }
        catch (SecurityException e) {
            // TODO: Handle Security Exception
            Log.e(TAG, "error: " + e);
        }
    }

    /**
     * class that represents which price options have been selected
     */
    public static class PriceRange{
        static ArrayList<Boolean> priceRange;

        //this block runs only once, when PriceRange is called for the first time
        static{
            priceRange = new ArrayList<>();
            for(int priceLevel = 0; priceLevel < 4; priceLevel++){
                priceRange.set(priceLevel, false);
            }
        }

        static void updatePriceIndication(int position, boolean isGoodPrice){
            try{
                priceRange.set(position, isGoodPrice);
            }catch(IndexOutOfBoundsException e){
                Log.e(TAG, "error: " + e);
            }
        }

        /**
         * Any changes made to priceRange should be done with 'updatePriceIndication'
         * @return a copy of Price Range
         */
        public boolean getPriceIndicationAt(int position){
            return priceRange.get(position);
        }
    }

    /**
     * Class that represents the search radius selected
     */
    public static class AreaRadius{
        private static int areaRadius;

        //I'm assuming here that the default radius will be 5 miles;
        static{
            areaRadius = 5;
        }

        void setRadius(int radius){
            areaRadius = 5;
        }

        public int getRadius(){
            return areaRadius;
        }
    }

    /**
     * Class representing user's location
     */
    public static class UserLocation {
        private static double longitude;
        private static double latitude;
        private static boolean grantedPermission;

        private static String longStr;
        private static String latStr;

        // Google Play's location service returns double values from -180.0 to 180.0
        static {
            longitude = 181.0;
            latitude = 181.0;
            grantedPermission = false;

            longStr = null;
            latStr = null;
        }

        void setLongitude(double _longitude) {
            longitude = _longitude;
        }

        void setLatitude(double _latitude) {
            latitude = _latitude;
        }

        void setGrantedPermission(boolean permission) {
            grantedPermission = permission;
        }

        void setUserLocation(double _longitude, double _latitude) {
            longitude = _longitude;
            latitude = _latitude;
        }

        void setUserLocationStrings(String _longitude, String _latitude) {
            longStr = _longitude;
            latStr = _latitude;
            Log.d(TAG, "New Location at LONG: " + longStr + " LAT: " + latStr);
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public boolean getPermission() {
            return grantedPermission;
        }

        public String getLongStr() {
            return longStr;
        }

        public String getLatStr() {
            return latStr;
        }
    }
}
