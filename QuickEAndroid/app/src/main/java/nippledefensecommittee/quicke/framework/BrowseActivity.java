package nippledefensecommittee.quicke.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import nippledefensecommittee.quicke.R;
import utility.BusinessList;

/**
 * Created by Devin on 8/18/2017.
 */

public class BrowseActivity extends AppCompatActivity implements FragmentChangeListener{
    private static final String TAG = BrowseActivity.class.getName();
    private Context mContext;
    private int mContainerId;
    private static final String FRAGTAG = "fragtag";
    private static final String BROWSETAG = "BrowseFragment";
    private static final String SWIPEVIEWTAG = "SwipeViewFragment";

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

        Fragment swipeViewFragment = getSupportFragmentManager()
                .findFragmentByTag(SWIPEVIEWTAG);
        Fragment browseFragment = getSupportFragmentManager()
                .findFragmentByTag(BROWSETAG);
        if(browseFragment != null && browseFragment.isVisible()){
            getSupportFragmentManager().putFragment(outState, FRAGTAG, browseFragment);
        }else if(swipeViewFragment != null && swipeViewFragment.isVisible()) {
            getSupportFragmentManager().putFragment(outState, FRAGTAG, swipeViewFragment);
        }
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
        if(savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, FRAGTAG);
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(fragment)
                    .commit();
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(mContainerId, new BrowseFragment(), BROWSETAG)
                    .commit();
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(mContainerId, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}
