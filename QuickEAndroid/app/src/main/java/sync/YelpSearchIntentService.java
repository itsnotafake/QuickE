package sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nippledefensecommittee.quicke.R;
import nippledefensecommittee.quicke.framework.MainActivity;
import utility.AuthToken;
import utility.BusinessList;

/**
 * Created by Devin on 8/15/2017.
 */

public class YelpSearchIntentService extends IntentService {
    private static final String TAG = YelpSearchIntentService.class.getName();
    private Context mContext;

    //default values for the two shared preference values (token & expiration)
    private static final String TOKEN_DEFAULT = "42069";
    private static final long TOKEN_EXP_DEFAULT = 999999999;

    //time converter static for handling token expiration date
    private static final long SECONDS_IN_10_DAYS = 864000;

    //values for dealing with the offset returned by Yelp. Only 50 values in a JSON return, out
    //of possible thousands, so offsets need to be used to get subsequent data
    public static String OFFSET_MULTIPLIER = "OFFSET_MULTIPLIER";
    private static int CURRENT_OFFSET_MULTIPLIER;
    public static int CURRENT_OFFSET_INCREMENT;
    private final int OFFSET = 50;
    private static int mTotalOffset;
    private static int mTotalBusinesses;

    public YelpSearchIntentService(){
        super("YelpSearchIntentService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        mContext = this;
        CURRENT_OFFSET_MULTIPLIER = intent.getIntExtra(OFFSET_MULTIPLIER, 0);
        CURRENT_OFFSET_INCREMENT = CURRENT_OFFSET_MULTIPLIER + 1;
        mTotalOffset = OFFSET * CURRENT_OFFSET_MULTIPLIER;
        getToken();
    }

    /**
     * Checks whether or not we have a token and token expiration already saved. If we don't,
     * it fetches a new token and saves it. If we do, it checks the expiration and if its expirying
     * soon then we fetch a new token. If we have a token and its not expiring then we use that
     * token to do our yelp request.
     * @return
     */
    private void getToken(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                getString(R.string.sharedpref_file_key),
                Context.MODE_PRIVATE
        );
        String token = sharedPref.getString(
                getString(R.string.sharedpref_token),
                TOKEN_DEFAULT
        );
        Long token_exp = sharedPref.getLong(
                getString(R.string.sharedpref_token_exp),
                TOKEN_EXP_DEFAULT
        );
        boolean hasTime = hasTime(token_exp);

        if(token.equals(TOKEN_DEFAULT) || !hasTime){
            getTokenFromYelp();
        }else{
            AuthToken.setAuthToken(token);
            doSync();
        }
    }

    private void getTokenFromYelp(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.auth_URI);

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AuthToken.setAuthToken(getToken(response));
                        doSync();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", AuthToken.getGrantType());
                params.put("client_id", AuthToken.getClientId());
                params.put("client_secret", AuthToken.getClientSecret());
                return params;
            }
        };
        queue.add(jsonObjRequest);
    }

    /**
     *We parse the JSON response and store the token and the expiration date. The expiration that
     * it returns is a long representing 180 days in seconds. We want the exact time, so we get the
     * current time in milliseconds, convert it to current time in seconds, and add it to the
     * value given to us by Yelp to know when exactly the token will expire.
     * @param response JSON response from yelp https://api.yelp.com/oauth2/token
     * @return we return the Token so that we can save it in AuthToken.
     */
    private String getToken(String response){
        try {
            JSONObject jsonObj = new JSONObject(response);
            SharedPreferences sharedPref = mContext.getSharedPreferences(
                    getString(R.string.sharedpref_file_key),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String token = jsonObj.getString("access_token");
            Long expiration = jsonObj.getLong("expires_in") + (System.currentTimeMillis()/1000);
            editor.putString(
                    getString(R.string.sharedpref_token),
                    token);
            editor.putLong(
                    getString(R.string.sharedpref_token_exp),
                    expiration);
            editor.commit();

            return token;
        }catch(JSONException e) {
            Log.e(TAG, "JSONExcpetion: " + e);
        }
        return null;
    }

    private void doSync(){
        //Assign all the variables we need to make a Yelp query
        double latitude = MainActivity.UserLocation.getLatitude();
        double longitude = MainActivity.UserLocation.getLongitude();
        int radius = MainActivity.AreaRadius.getRadius();
        String price = MainActivity.PriceRange.getPriceRangeString();
        final boolean isOpenNow = true;

        String categories = mealsToCategoryString(
                MainActivity
                        .MealSelection
                        .getSelectedCollection()
        );
        //printValues(latitude,longitude,radius,categories,price,isOpenNow);

        Uri.Builder yelpSearchURL = new Uri.Builder();
        yelpSearchURL.scheme(mContext.getString(R.string.yelpSearchScheme))
                .authority(mContext.getString(R.string.yelpSearchAuthority))
                .appendPath(mContext.getString(R.string.yelpSearchPathV3))
                .appendPath(mContext.getString(R.string.yelpSearchPathBuss))
                .appendPath(mContext.getString(R.string.yelpSearchPathSearch))
                .appendQueryParameter(mContext.getString(R.string.search_latitude),
                        String.valueOf(latitude))
                .appendQueryParameter(mContext.getString(R.string.search_longitude),
                        String.valueOf(longitude))
                .appendQueryParameter(mContext.getString(R.string.search_radius),
                        String.valueOf(radius))
                .appendQueryParameter(mContext.getString(R.string.search_categories),
                        categories)
                .appendQueryParameter(mContext.getString(R.string.search_limit),
                        String.valueOf(OFFSET))
                .appendQueryParameter(mContext.getString(R.string.search_offset),
                        String.valueOf(mTotalOffset))
                .appendQueryParameter(mContext.getString(R.string.search_price),
                        price)
                .appendQueryParameter(mContext.getString(R.string.search_open),
                        String.valueOf(isOpenNow));
        String url = yelpSearchURL.build().toString();
        //Log.e(TAG, url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e(TAG, response);
                        if(hasNotExceededTotal(response)) {
                            BusinessList.addBulk(response, mContext);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth  = "Bearer " + AuthToken.getAuthToken();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjRequest);
    }

    /**
     * Translates the collection of all selected meals into a string of comma delimited
     * categories that will be used in the Yelp query
     * @param meals Collection<String> of all selected meal types
     * @return a String that represents all categories we are searching by
     */
    private String mealsToCategoryString(Collection<String> meals){
        StringBuilder categoryString = new StringBuilder();
        for(String s : meals){
            if(categoryString.length() == 0){
                categoryString.append(mealToCategoryString(s));
            }else{
                categoryString.append(",");
                categoryString.append(mealToCategoryString(s));
            }
        }
        return categoryString.toString();
    }

    /**
     * Takes the meal String and hooks it up to its corresponding int[] that contains
     * the ids of all the categories corresponding to that meal
     * @param s meal String
     * @return a string representing all the categories of that meal, comma delimited
     */
    private String mealToCategoryString(String s){
        int[] myCategories;
        switch(s){
            case "American":
                myCategories = MainActivity.MealSelection.selectionAmerican;
                break;
            case "Latino":
                myCategories = MainActivity.MealSelection.selectionLatino;
                break;
            case "Asian":
                myCategories = MainActivity.MealSelection.selectionAsian;
                break;
            case "European":
                myCategories = MainActivity.MealSelection.selectionEuropean;
                break;
            case "Indian":
                myCategories = MainActivity.MealSelection.selectionIndian;
                break;
            case "Middle Eastern":
                myCategories = MainActivity.MealSelection.selectionMiddleEastern;
                break;
            case "African":
                myCategories = MainActivity.MealSelection.selectionAfrican;
                break;
            case "Coffee and Tea":
                myCategories = MainActivity.MealSelection.selectionCoffeeAndTea;
                break;
            case "Milk Tea":
                myCategories = MainActivity.MealSelection.selectionMilkTea;
                break;
            case "Alcohol":
                myCategories = MainActivity.MealSelection.selectionAlcohol;
                break;
            case "Smoothie":
                myCategories = MainActivity.MealSelection.selectionSmoothie;
                break;
            default:
                throw new IllegalArgumentException("Unrecognized meal, cannot convert to" +
                        " a category string");
        }
        return categoryArrayToString(myCategories);
    }

    /**
     * Takes the int[] that contains ids of categories and translates those ids
     * into a comma delimited String using the values in strings.xml
     * @param myCategories int[] holding strings.xml ids
     * @return comma delmited string of categories
     */
    private String categoryArrayToString(int[] myCategories){
        StringBuilder categoryString = new StringBuilder();
        for(int i : myCategories){
            if(categoryString.length() == 0){
                categoryString.append(mContext.getString(i));
            }else{
                categoryString.append(",");
                categoryString.append(mContext.getString(i));
            }
        }
        return categoryString.toString();
    }

    /**
     *
     * @param tokenExpiration at the time the token was created tokenexpiration = current time in
     *                        seconds + 180 days in seconds (~15,500,000)
     * @return if the current time is within 10 days (~864,000 seconds) of the expiration or past
     * the expiration, then return false. Else, return true, we can still use the token
     */
    private boolean hasTime(Long tokenExpiration){
        Long currentTime = System.currentTimeMillis() / 1000;
        if(tokenExpiration == TOKEN_EXP_DEFAULT ||
                tokenExpiration - currentTime < SECONDS_IN_10_DAYS){
            return false;
        }
        return true;
    }

    /**
     * Looking at our current offset, total businesses per offset, and total businesses,
     * determines whether or not this next batch of businesses would exceed the total
     * businesses returned for this query. If it does, we do not issue the queury.
     * @param response The JSON response from Yelp, we use it to determine total businesses
     * @return return a boolean that signifies if the next batch would exceed the total
     * businesses
     */
    private boolean hasNotExceededTotal(String response){
        try{
            JSONObject yelpResponseJSON = new JSONObject(response);
            mTotalBusinesses = yelpResponseJSON
                    .getInt(mContext.getString(R.string.businesslist_total));
            Log.e(TAG, "total is " + mTotalBusinesses);
            return (mTotalBusinesses > mTotalOffset);
        }catch(JSONException e){
            Log.e(TAG, "Trouble parsing String response from Yelp to JSON: " + e);
            return true;
        }
    }

    /**
     * nice little helper method for printing out all values. Helps with checking
     */
    private void printValues(double latitude, double longitude, int radius,
                             String categories, String price, boolean isOpenNow){
        Log.e(TAG, "latitude is: " + latitude);
        Log.e(TAG, "longitude is: " + longitude);
        Log.e(TAG, "radius is: " + radius);
        Log.e(TAG, "categories is: " + categories);
        Log.e(TAG, "totalOffset is: " + mTotalOffset);
        Log.e(TAG, "price is: " + price);
        Log.e(TAG, "isOpenNow is: " + isOpenNow);
    }
}
