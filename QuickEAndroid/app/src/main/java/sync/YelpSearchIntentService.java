package sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.Collection;

import nippledefensecommittee.quicke.framework.MainActivity;

/**
 * Created by Devin on 8/15/2017.
 */

public class YelpSearchIntentService extends IntentService {
    private static final String TAG = YelpSearchIntentService.class.getName();
    public static String OFFSET_MULTIPLIER = "OFFSET_MULTIPLIER";
    private final int OFFSET = 20;

    public YelpSearchIntentService(){
        super("YelpSearchIntentService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        //Assign all the variables we need to make a Yelp query
        double latitude = MainActivity.UserLocation.getLatitude();
        double longitude = MainActivity.UserLocation.getLongitude();
        int radius = MainActivity.AreaRadius.getRadius();
        Collection<String> categories = MainActivity.MealSelection.getSelectedCollection();
        int totalOffset = OFFSET * intent.getIntExtra(OFFSET_MULTIPLIER, 0);
        String price = MainActivity.PriceRange.getPriceRangeString();
        final boolean isOpenNow = true;
    }

    /**
     * nice little helper method for printing out all values. Helps with checking
     */
    private void printValues(double latitude, double longitude, int radius,
                             Collection<String> categories, int totalOffset,
                             String price, boolean isOpenNow){
        Log.e(TAG, "latitude is: " + latitude);
        Log.e(TAG, "longitude is: " + longitude);
        Log.e(TAG, "radius is: " + radius);
        Log.e(TAG, "categories is: " + categories);
        Log.e(TAG, "totalOffset is: " + totalOffset);
        Log.e(TAG, "price is: " + price);
        Log.e(TAG, "isOpenNow is: " + isOpenNow);
    }
}
