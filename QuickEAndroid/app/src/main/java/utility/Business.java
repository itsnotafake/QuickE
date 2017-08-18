package utility;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Devin on 8/17/2017.
 */

public class Business {
    private static final String TAG = Business.class.getName();
    private Context mContext;

    private String yelpId;
    private String name;
    private String image_url;
    private String url;

    private double rating;
    private int price;
    private String phone_number;

    private double latitude;
    private double longitude;
    private double distance;

    public Business(HashMap<String, String> businessValues, Context context){
        Log.e(TAG, businessValues.toString());
    }
}

