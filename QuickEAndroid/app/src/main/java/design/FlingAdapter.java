package design;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

import nippledefensecommittee.quicke.R;
import utility.Business;
import utility.BusinessList;

/**
 * Created by Devin on 8/18/2017.
 */

public class FlingAdapter extends BaseAdapter {
    private static final String TAG = FlingAdapter.class.getName();
    private Fragment mFragment;
    private Bundle mSavedInstanceState;

    public FlingAdapter(Fragment fragment, Bundle savedInstanceState){
        mFragment = fragment;
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    public int getCount(){
        return BusinessList.size();
    }

    @Override
    public Business getItem(int position){
        return BusinessList.getBusiness(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){
        if(convertView == null){
            convertView = mFragment
                    .getLayoutInflater(mSavedInstanceState)
                    .inflate(R.layout.browse_slingitem, container, false);
        }
        Business business = BusinessList.getBusiness(position);
        TextView businessName = (TextView) convertView.findViewById(R.id.sling_title);
        TextView businessDistance = (TextView) convertView.findViewById(R.id.sling_distance);
        ImageView businessImage = (ImageView) convertView.findViewById(R.id.sling_image);
        ImageView yelpImage = (ImageView) convertView.findViewById(R.id.sling_logo);

        businessName.setText(business.getName());
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double distance = Double.valueOf(decimalFormat.format(business.getDistance()));
        String distanceAddOn;
        if(distance == 1) {
            distanceAddOn = String.valueOf(distance) + " Mile Away";
        }else{
            distanceAddOn = String.valueOf(distance) + " Miles Away";
        }
        businessDistance.setText(distanceAddOn);
        Glide.with(mFragment)
                .load(business.getImageUrl())
                .into(businessImage);
        Glide.with(mFragment)
                .load(R.drawable.yelp)
                .into(yelpImage);
        return convertView;
    }
}
