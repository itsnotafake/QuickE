package design;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import nippledefensecommittee.quicke.R;
import utility.Business;
import utility.BusinessList;

/**
 * Created by Devin on 8/18/2017.
 */

public class FlingAdapter extends BaseAdapter {
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
        TextView businessName = (TextView) convertView.findViewById(R.id.sling_title);
        ImageView businessImage = (ImageView) convertView.findViewById(R.id.sling_image);

        businessName.setText(BusinessList.getBusiness(position).getName());
        Glide.with(mFragment)
                .load(BusinessList.getBusiness(position).getImageUrl())
                .into(businessImage);
        return convertView;
    }
}
