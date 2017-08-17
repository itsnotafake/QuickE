package nippledefensecommittee.quicke.framework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import nippledefensecommittee.quicke.BuildConfig;
import nippledefensecommittee.quicke.R;
import sync.AuthIntentService;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult>,
        FragmentChangeListener {

    private static final String TAG = MainActivity.class.getName();
    private static final String TAG2 = "WTF";
    //key for our out/in statebundle that determines which fragment we show in onCreate().
    private static final String FRAGCHECK = "basefragcheck";

    private Context mContext;
    private int mContainerId;

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
    private FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mContainerId = R.id.fragment_container;

        initializeAppBar();
        initializeFragment(savedInstanceState);

        locationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mRequestingLocation = false;
        mLastUpdateTime = "";

        //createGoogleApiClient();
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
                Fragment fragment = new BaseFragment();
                replaceFragment(fragment, false);
                return true;
            case R.id.action_test_rec:
                Intent intent = new Intent(MainActivity.this, RecommendationActivity.class);
                startActivity(intent);
                return true;
            default:
                Log.e(TAG, "Unrecognized item onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Appbar initialization removed from onCreate to avoid clutter
     */
    private void initializeAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.main_text));
        setSupportActionBar(toolbar);
    }

    /**
     * Loading base fragment into Activity. Removed logic from onCreate
     * to avoid clutter
     * @param savedInstanceState used to determine which fragment to instantiate. If the screen
     * was rotated when displaying FoodSelectFragment, then we want to display FoodSelectFragment
     */
    private void initializeFragment(Bundle savedInstanceState){
        boolean isFoodSelectFragment;
        try{
            isFoodSelectFragment = savedInstanceState.getBoolean(FRAGCHECK);
        }catch(NullPointerException e){
            isFoodSelectFragment = false;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        if(!isFoodSelectFragment){
            fragment = new BaseFragment();

        }else{
            fragment = new FoodSelectFragment();
        }
        fragmentTransaction.add(mContainerId, fragment);
        fragmentTransaction.commit();
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

        locationStartup();
        /*
        if (mGoogleApiClient.isConnected()) {
            checkLocationSettings();
        } else {
            locationStartup();
        } */
    }

    @Override
    protected void onStop() {
        super.onStop();

        locationStartup();
        /*
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        } */
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Fragment baseFragment = getSupportFragmentManager().findFragmentByTag("BaseFragment");
        if(baseFragment != null && baseFragment.isVisible()){
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        boolean isFoodSelectFragment = false;
        Fragment foodSelectFragment = getSupportFragmentManager().findFragmentByTag("FoodSelectFragment");
        if(foodSelectFragment != null && foodSelectFragment.isVisible()){
            isFoodSelectFragment = true;
        }
        outState.putBoolean(FRAGCHECK, isFoodSelectFragment);
    }

    private void updateUserLocation() {
        double longitude = mLocation.getLongitude();
        double latitude = mLocation.getLatitude();

        userLoc.setUserLocation(longitude, latitude);
        userLoc.setUserLocationStrings(mLocation.convert(longitude, Location.FORMAT_DEGREES), mLocation.convert(latitude, Location.FORMAT_DEGREES));
        userLoc.setGrantedPermission(true);
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
                        showSnack("Unable to access location services. Closing application.");

                        // TODO
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        //System.exit(0);
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
     *
     * @param fragment fragment to be swapped in
     * @param addToBackStack true if you want to add to the backStack
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mContainerId, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
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

        static boolean hasSelection(){
            return quickEUsage[0] || quickEUsage[1];
        }
    }

    /**
     * class that represents which price options have been selected
     * The radius is set in miles, but is converted to meters (as required by Yelp) when
     * fetched
     */
    public static class PriceRange{
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

        static boolean hasSelection(){
            return getPriceIndicationAt(0) || getPriceIndicationAt(1) || getPriceIndicationAt(2) ||
                    getPriceIndicationAt(3);
        }

        public static String getPriceRangeString(){
            StringBuilder price = new StringBuilder();
            int counter = 1;
            for(boolean b : priceRange){
                if((price.length() == 0) && b){
                    price.append(counter);
                    counter++;
                }else if(b){
                    price.append(",");
                    price.append(counter);
                    counter++;
                }else{
                    counter++;
                }
            }
            return price.toString();
        }
    }

    /**
     * Class that represents the search radius selected
     */
    public static class AreaRadius{
        private static int areaRadius;
        private static final int MILES_TO_METERS = 1600;

        //I'm assuming here that the default radius will be 5 miles;
        static{
            areaRadius = 5;
        }

        static void setRadius(int radius){
            areaRadius = radius;
        }

        /**
         *
         * @return our radius in meters. There are 1609.34 meters ina mile but the max value is
         * 40,000 meters which Yelp says corresponds to 25 miles, so we are doing 1 mile is equal
         * to 1600 meters
         */
        public static int getRadius(){
            return areaRadius * MILES_TO_METERS;
        }
    }

    /**
     * Static class representing the different foods and drinks we will be presented with on the
     * FoodSelection fragment screen.
     */
    public static class MealSelection{
        private static int[] foodSelection;
        private static int[] beverageSelection;

        private static HashMap<String, String> selected;

        public static final int[] selectionAmerican;
        public static final int[] selectionLatino;
        public static final int[] selectionAsian;
        public static final int[] selectionIndian;
        public static final int[] selectionMiddleEastern;
        public static final int[] selectionEuropean;
        public static final int[] selectionAfrican;

        public static final int[] selectionCoffeeAndTea;
        public static final int[] selectionMilkTea;
        public static final int[] selectionAlcohol;
        public static final int[] selectionSmoothie;

        static{
            foodSelection = new int[]{
                    R.string.mealselection_american,
                    R.string.mealselection_latino,
                    R.string.mealselection_asian,
                    R.string.mealselection_indian,
                    R.string.mealselection_middleeast,
                    R.string.mealselection_european,
                    R.string.mealselection_african
            };

            beverageSelection = new int[]{
                    R.string.mealselection_coffeetea,
                    R.string.mealselection_milktea,
                    R.string.mealselection_alcohol,
                    R.string.mealselection_smoothie,
            };

            selected = new HashMap<>();

            selectionAmerican = new int[]{
                    R.string.american_newamerican,
                    R.string.american_tradamerican,
                    R.string.american_bbq,
                    R.string.american_burgers,
                    R.string.american_cajun,
                    R.string.american_diners,
                    R.string.american_hotdog,
                    R.string.american_sandwiches,
                    R.string.american_soulfood,
                    R.string.american_southern,
                    R.string.american_steak
            };

            selectionLatino = new int[]{
                    R.string.latino_argentine,
                    R.string.latino_brazilian,
                    R.string.latino_cuban,
                    R.string.latino_honduran,
                    R.string.latino_latin,
                    R.string.latino_mexican,
                    R.string.latino_nicaraguan,
                    R.string.latino_peruvian
            };

            selectionAsian = new int[]{
                    R.string.asian_asianfusion,
                    R.string.asian_burmese,
                    R.string.asian_cambodian,
                    R.string.asian_chinese,
                    R.string.asian_dumplings,
                    R.string.asian_filipino,
                    R.string.asian_hotpot,
                    R.string.asian_hkcafe,
                    R.string.asian_indonesian,
                    R.string.asian_japanese,
                    R.string.asian_korean,
                    R.string.asian_malaysian,
                    R.string.asian_mongolian,
                    R.string.asian_noodles,
                    R.string.asian_singaporean,
                    R.string.asian_srilankan,
                    R.string.asian_sushi,
                    R.string.asian_taiwanese,
                    R.string.asian_thai,
                    R.string.asian_vietnamese
            };

            selectionIndian = new int[]{
                    R.string.indian_bangladeshi,
                    R.string.indian_indpak,
                    R.string.indian_pakistani
            };

            selectionMiddleEastern = new int[]{
                    R.string.middleeastern_afghani,
                    R.string.middleastern_arabian,
                    R.string.middleeastern_halal,
                    R.string.middleeastern_kebab,
                    R.string.middleeastern_mideastern,
                    R.string.middleeastern_persian,
                    R.string.middleeastern_syrian
            };

            selectionEuropean = new int[]{
                    R.string.european_austrian,
                    R.string.european_baguettes,
                    R.string.european_belgian,
                    R.string.european_british,
                    R.string.european_catalan,
                    R.string.european_czech,
                    R.string.european_fishnchips,
                    R.string.european_french,
                    R.string.european_german,
                    R.string.european_greek,
                    R.string.european_hungarian,
                    R.string.european_iberian,
                    R.string.european_irish,
                    R.string.european_italian,
                    R.string.european_mediterranean,
                    R.string.european_modern_european,
                    R.string.european_pizza,
                    R.string.european_polish,
                    R.string.european_portuguese,
                    R.string.european_russian,
                    R.string.european_scandinavian,
                    R.string.european_scottish,
                    R.string.european_slovakian,
                    R.string.european_spanish,
                    R.string.european_tapas,
                    R.string.european_ukrainian,
                    R.string.european_uzbek
            };

            selectionAfrican = new int[]{
                    R.string.african_african,
                    R.string.african_ethiopian
            };

            selectionCoffeeAndTea = new int[]{
                    R.string.coffee_coffee,
                    R.string.coffee_cafes,
                    R.string.coffee_tea
            };

            selectionMilkTea = new int[]{
                    R.string.milktea_bubbletea
            };

            selectionAlcohol = new int[]{
                    R.string.alcohol_alcohol,
                    R.string.alcohol_breweries,
                    R.string.alcohol_wineries
            };

            selectionSmoothie = new int[]{
                    R.string.smoothie_juicebars
            };
        }

        public static int[] getFoodSelection(){
            return foodSelection;
        }

        public static int[] getBeverageSelection(){
            return beverageSelection;
        }

        public static void addSelected(String meal){
            if(!selected.containsKey(meal)) {
                selected.put(meal, meal);
            }
        }

        public static void removeSelected(String meal){
            try{
                selected.remove(meal);
            }catch(NullPointerException e){
                Log.e(TAG, meal + " does not exist in Hashmap 'selected'");
            }
        }

        static boolean selectedIsEmpty(){
            return selected.isEmpty();
        }

        static void clear(){
            selected.clear();
        }

        public static Collection<String> getSelectedCollection(){
            return selected.values();
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

        public static double getLongitude() {
            return longitude;
        }

        public static double getLatitude() {
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
