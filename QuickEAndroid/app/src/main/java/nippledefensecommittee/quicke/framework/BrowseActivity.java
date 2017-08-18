package nippledefensecommittee.quicke.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import nippledefensecommittee.quicke.R;

/**
 * Created by Devin on 8/18/2017.
 */

public class BrowseActivity extends AppCompatActivity {
    private static final String TAG = BrowseActivity.class.getName();
    private Context mContext;
    private int mContainerId;
    private static final String FRAGCHECK = "browsefragcheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_browse);
        mContainerId = R.id.browse_fragment_container;

        initializeAppBar();
        initializeFragment(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        boolean isSwipeViewFragment = false;
        Fragment swipeViewFragment = getSupportFragmentManager().findFragmentByTag("SwipeViewFragment");
        if(swipeViewFragment != null && swipeViewFragment.isVisible()){
            isSwipeViewFragment = true;
        }
        outState.putBoolean(FRAGCHECK, isSwipeViewFragment);
    }

    /**
     * Appbar initialization removed from onCreate to avoid clutter
     */
    private void initializeAppBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.browse_toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.main_text));
        setSupportActionBar(toolbar);
    }

    /**
     * Loading base fragment into Activity. Removed logic from onCreate
     * to avoid clutter
     * @param savedInstanceState used to determine which fragment to instantiate. If the screen
     * was rotated when displaying SwipeViewFragment, then we want to display SwipeViewFragment
     */
    private void initializeFragment(Bundle savedInstanceState){
        boolean isSwipeViewFragment;
        try{
            isSwipeViewFragment = savedInstanceState.getBoolean(FRAGCHECK);
        }catch(NullPointerException e){
            isSwipeViewFragment = false;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        if(!isSwipeViewFragment){
            fragment = new BrowseFragment();

        }else{
            fragment = new SwipeViewFragment();
        }
        fragmentTransaction.add(mContainerId, fragment);
        fragmentTransaction.commit();
    }
}
