package utility;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import nippledefensecommittee.quicke.R;

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
    private int reviewCount;
    private String price;
    private String phone_number;

    private double latitude;
    private double longitude;
    private double distance;

    public Business(HashMap<String, String> businessMap, Context context){
        mContext = context;

        yelpId = businessMap.get(context.getString(R.string.businesslist_id));
        name = businessMap.get(context.getString(R.string.businesslist_name));
        image_url = businessMap.get(context.getString(R.string.businesslist_imageurl));
        url = businessMap.get(context.getString(R.string.businesslist_url));
        rating = Double.parseDouble(
                businessMap.get(context.getString(R.string.businesslist_rating)));
        reviewCount = Integer.parseInt(
                businessMap.get(context.getString(R.string.businesslist_reviewCount)));
        price = businessMap.get(context.getString(R.string.businesslist_price));
        phone_number = businessMap.get(context.getString(R.string.businesslist_phonenumber));
        latitude = Double.parseDouble(
                businessMap.get(context.getString(R.string.businesslist_latitude)));
        longitude = Double.parseDouble(
                businessMap.get(context.getString(R.string.businesslist_longitude)));
        distance = (Double.parseDouble(
                businessMap.get(context.getString(R.string.businesslist_distance)))) / 1609;
    }

    public String getName(){
        return name;
    }

    public String getImageUrl(){
        return image_url;
    }

    public String getUrl(){
        return url;
    }

    public int getRatingDrawable(){
        if(rating == 0.0){
            return R.drawable.stars_regular_0;
        }else if(rating == 1.0){
            return R.drawable.stars_regular_1;
        }else if(rating == 1.5){
            return R.drawable.stars_regular_1_half;
        }else if(rating == 2.0){
            return R.drawable.stars_regular_2;
        }else if(rating == 2.5){
            return R.drawable.stars_regular_2_half;
        }else if(rating == 3.0){
            return R.drawable.stars_regular_3;
        }else if(rating == 3.5){
            return R.drawable.stars_regular_3_half;
        }else if(rating == 4.0){
            return R.drawable.stars_regular_4;
        }else if(rating == 4.5){
            return R.drawable.stars_regular_4_half;
        }else if(rating == 5.0){
            return R.drawable.stars_regular_5;
        }else{
            Log.e(TAG, "Bad rating value for business: " + getName());
            return R.drawable.stars_regular_0;
        }
    }

    public int getReviewCount(){
        return reviewCount;
    }

    public String getPrice(){
        return price;
    }

    public double getDistance(){
        return distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

