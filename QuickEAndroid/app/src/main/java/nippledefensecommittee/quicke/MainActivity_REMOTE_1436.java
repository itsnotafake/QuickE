package nippledefensecommittee.quicke;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.SnackbarContentLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.Snackbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    private static final String TAG = MainActivity.class.getName();
    private static final String TAG2 = "WTF";

    private static final int LOCATION_CODE = 123;

    // Desired and fastest interval rate for location updates
    public static final long UPDATE_INTERVAL_BASE = 10000;
    public static final long UPDATE_INTERVAL_FAST = UPDATE_INTERVAL_BASE / 2;

    // Bundle keys for storing activity state
    // Most likely unnecessary
    protected final String KEY_REQ_LOC = "requesting_location_updates";
    protected final String KEY_LOC = "location";
    protected final String KEY_TIME_UPDATED = "last_time_updated";

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationReq;
    protected LocationSettingsRequest mLocationSettingsReq;
    protected Location mLocation;

    protected boolean mRequestingLocation;
    protected String mLastUpdateTime;

    private static UserLocation userLoc = new UserLocation();
    private static FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAppBar();

        locationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mRequestingLocation = false;
        mLastUpdateTime = "";

        createGoogleApiClient();
        createLocationRequest();
        createLocationSettingsRequest();
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

        locationStartup();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            checkLocationSettings();
        } else {
            locationStartup();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void updateUserLocation() {
        double longitude = mLocation.getLongitude();
        double latitude = mLocation.getLatitude();

        userLoc.setUserLocation(longitude, latitude);
        userLoc.setUserLocationStrings(mLocation.convert(longitude, Location.FORMAT_DEGREES), mLocation.convert(latitude, Location.FORMAT_DEGREES));
        userLoc.setGrantedPermission(true);
    }

    // TODO: DECIDE WHETHER OR NOT TO USE GOOGLE API CLIENT
    private void locationStartup() {
        if (!checkUserPermission()) {
            requestUserPermission();
        } else {
            checkLocationSettings2();
            //mGoogleApiClient.connect();
        }
    }

    protected synchronized void createGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationReq = new LocationRequest();
        mLocationReq.setInterval(UPDATE_INTERVAL_BASE);
        mLocationReq.setFastestInterval(UPDATE_INTERVAL_FAST);
        mLocationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // TODO: See if this works
        //mLocationReq.setNumUpdates(1);
    }

    protected void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder mBuilder = new LocationSettingsRequest.Builder();
        mBuilder.addLocationRequest(mLocationReq);
        mLocationSettingsReq = mBuilder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, mLocationSettingsReq);
        result.setResultCallback(this);
    }

    protected void checkLocationSettings2() {
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(mLocationSettingsReq);

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getUserLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings invalid: Showing user dialog to resolve.");
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, LOCATION_CODE);
                        } catch (IntentSender.SendIntentException ex) {
                            Log.e(TAG, "Error occurred when sending intent: " + ex);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are invalid and cannot be fixed.");
                        break;
                }
            }
        });
    }

    /**
     * Checks if the user has given permission to access their location.
     */
    private boolean checkUserPermission() {
        int userPermission = ContextCompat.checkSelfPermission(this,
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

        final String[] permissionFine = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        if (!rationale) {
            Log.i(TAG, "Requesting permission from user.");

            ActivityCompat.requestPermissions(MainActivity.this, permissionFine, LOCATION_CODE);
        } else {
            Log.i(TAG, "Providing context for location use to user.");

            showSnack(R.string.rationale, android.R.string.ok, new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                   ActivityCompat.requestPermissions(MainActivity.this, permissionFine, LOCATION_CODE);
               }
            });
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
        locationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLocation = task.getResult();
                    updateUserLocation();
                    showSnack("Location found at LONG: " + userLoc.getLongStr() + " LAT: " + userLoc.getLatStr());
                } else {
                    Log.d(TAG, "Unable to detect location.");
                    showSnack(getString(R.string.no_location));
                }
            }
        });

        /*
        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location){
                if (location != null) {
                    mLocation = location;
                    updateUserLocation();
                } else {
                    Log.d(TAG2, "Unable to detect location.");
                    showSnack(getString(R.string.no_location));
                }

            }
        }); */
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

                    /*if (mGoogleApiClient.isConnected() == false) {
                        mGoogleApiClient.connect();
                    } else {
                        checkLocationSettings();
                    }*/
                    checkLocationSettings2();
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

    @Override
    public void onResult(LocationSettingsResult locSetRes) {
        final Status locStatus = locSetRes.getStatus();
        switch (locStatus.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "Location settings valid: Attempting to get user location.");
                getUserLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings invalid: Showing user dialog to resolve.");
                try {
                    locStatus.startResolutionForResult(MainActivity.this, LOCATION_CODE);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Error occurred when sending intent: " + e);
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are invalid and cannot be fixed.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case LOCATION_CODE:
                switch (resCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User made required location settings changes.");
                        getUserLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User did not make required changes.");
                        break;
                }
                break;
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        checkLocationSettings();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUserLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: " + result.getErrorCode());
        if (result.getErrorCode() == 2) {
            showSnack(getString(R.string.updateGPS));
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
