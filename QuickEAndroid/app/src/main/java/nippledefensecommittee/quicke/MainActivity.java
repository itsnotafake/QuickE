package nippledefensecommittee.quicke;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.internal.SnackbarContentLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.design.widget.Snackbar;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private static UserLocation userLoc = new UserLocation();
    private FusedLocationProviderClient locationClient;
    private static final int LOCATION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAppBar();

        locationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_refresh:
                //TODO
                return true;
            default:
                Log.e(TAG, "Unrecognized item onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
    }

    /**
     * On application startup the app will check for location permission.
     * If permission is not yet granted the app will ask for permission.
     * Otherwise app will get user location.
     */
    @Override
    public void onStart() {
        super.onStart();

        if (!checkUserPermission()) {
            requestUserPermission();
        } else {
            getUserLocation();
        }
    }

    /**
     * Creates a snackbar to display a message to user.
     */
    private void showSnack(final String msg) {
        View mainContainer = findViewById(R.id.main_container);
        if (mainContainer != null) {
            Snackbar.make(mainContainer, msg, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a snackbar to display a message, set an action, and wait for user response.
     */
    private void showSnack(final int textID, final int actionID, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content), getString(textID),
                Snackbar.LENGTH_INDEFINITE).setAction(getString(actionID), listener).show();
    }

    /**
     * Checks if the user has given permission to access their location.
     */
    private boolean checkUserPermission() {
        int userPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return userPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permission to access user location.
     *
     * Depending on if extra context is necessary, function will either directly request permission
     * or display a snackbar with context before asking for permission.
     */
    private void requestUserPermission() {
        boolean rationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        final String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        if (!rationale) {
            Log.i(TAG, "Requesting permission from user.");

            ActivityCompat.requestPermissions(MainActivity.this, permission, LOCATION_CODE);
        } else {
            Log.i(TAG, "Providing context for location use to user.");

            showSnack(R.string.rationale, android.R.string.ok, new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                   ActivityCompat.requestPermissions(MainActivity.this, permission, LOCATION_CODE);
               }
            });
        }
    }

    /**
     * User permission response handler.
     *
     * If permission is granted handler will obtain last known user location.
     * If permission is denied handler will display a message asking user to reconsider and
     * redirect them to the settings page.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        Log.i(TAG, "Handling permission request result.");

        switch (requestCode) {
            case LOCATION_CODE:
                if (results.length <= 0) {
                    Log.i(TAG, "User interaction was cancelled.");
                } else if (results[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission Granted!");
                    getUserLocation();
                } else {
                    Log.i(TAG, "Permission Denied!");
                    showSnack(R.string.permission_denied, R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, results);
        }
    }

    /**
     * Uses Google Play Services to obtain last known user location, which is usually
     * the user's current location.
     *
     * Populates fields of static UserLocation object.
     */
    @SuppressWarnings("MissingPermission")
    private void getUserLocation() {
        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location){
                if (location != null) {
                    final double longitude = location.getLongitude();
                    final double latitude = location.getLatitude();

                    userLoc.setUserLocation(longitude, latitude);
                    userLoc.setUserLocationStrings(location.convert(longitude, Location.FORMAT_DEGREES), location.convert(latitude, Location.FORMAT_DEGREES));
                    userLoc.setGrantedPermission(true);
                } else {
                    Log.d(TAG, "Unable to detect location.");
                    showSnack(getString(R.string.no_location));
                }
            }
        });
    }

    /**
     * A simple boolean[2] to indicate whether the user is looking for a dining or drinking
     * experience. As of right now the user can select both. Perhaps in the future this will
     * be change to just one
     */
    static class QuickEUsage{
        static boolean[] quickEUsage;

        //initialize boolean array to false false
        static{
            quickEUsage = new boolean[2];
            quickEUsage[0] = false;
            quickEUsage[1] = false;
        }

        static void updateQuickEUsage(int position, boolean isGoodOption){
            quickEUsage[position] = isGoodOption;
        }

        static boolean getQuickEUsageAt(int position){
            return quickEUsage[position];
        }
    }

    /**
     * class that represents which price options have been selected
     */
    static class PriceRange{
        static boolean[] priceRange;

        //this block runs only once, when PriceRange is called for the first time
        static{
            priceRange = new boolean[4];
            for(int priceLevel = 0; priceLevel < 4; priceLevel++){
                priceRange[priceLevel] = false;
            }
        }

        static void updatePriceIndication(int position, boolean isGoodPrice){
            try{
                priceRange[position] = isGoodPrice;
            }catch(IndexOutOfBoundsException e){
                Log.e(TAG, "error: " + e);
            }
        }

        /**
         * Any changes made to priceRange should be done with 'updatePriceIndication'
         * @return a copy of Price Range
         */
        static boolean getPriceIndicationAt(int position){return priceRange[position];}
    }

    /**
     * Class that represents the search radius selected
     */
    static class AreaRadius{
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
