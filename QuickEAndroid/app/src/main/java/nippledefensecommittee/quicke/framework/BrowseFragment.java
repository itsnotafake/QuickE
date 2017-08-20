package nippledefensecommittee.quicke.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.lorentzos.flingswipe.FlingCardListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import design.FlingAdapter;
import nippledefensecommittee.quicke.R;
import utility.Business;
import utility.BusinessList;

/**
 * Created by Devin on 8/18/2017.
 */

public class BrowseFragment extends Fragment {
    private static final String TAG = BrowseFragment.class.getName();
    public static final String BUSINESS_BROADCAST = "myBusinessListBroadcastListener";

    private SwipeFlingAdapterView mFlingContainer;
    private FlingAdapter mAdapter;
    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            initializeBroadcastReceiver();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        mProgress = (ProgressBar) view.findViewById(R.id.browse_progress);
        initializeSwipeFling(view, savedInstanceState);
        return view;
    }

    @Override
    public String toString(){
        return "BrowseFragment";
    }

    private void initializeBroadcastReceiver(){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mAdapter.notifyDataSetChanged();
                mProgress.setVisibility(View.GONE);
                mFlingContainer.setVisibility(View.VISIBLE);

            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                receiver, new IntentFilter(BUSINESS_BROADCAST));
    }

    private void initializeSwipeFling(View view, Bundle savedInstanceState){
        mFlingContainer =
                (SwipeFlingAdapterView) view.findViewById(R.id.browse_swipefling);

        mAdapter = new FlingAdapter(this, savedInstanceState);
        Log.e(TAG, "adapter size is: " + mAdapter.getCount());

        mFlingContainer.setAdapter(mAdapter);
        mFlingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener(){
            @Override
            public void removeFirstObjectInAdapter(){
                BusinessList.removeBusiness(0);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject){
            }

            @Override
            public void onRightCardExit(Object dataObject){
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter){

            }

            @Override
            public void onScroll(float scroll){

            }
        });
    }
}
